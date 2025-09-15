package hi_light.word_scramble.service;

import hi_light.common.GameRoom;
import hi_light.common.Player;
import hi_light.word_scramble.model.WordScrambleGame;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.stream.Collectors;
import java.util.Comparator;

// personal_words 폴더의 엔티티와 리포지토리를 사용하기 위한 import
import hi_light.personal_words.repository.PersonalWordsRepository; // <- 기존 리포지토리의 경로
import hi_light.personal_words.entity.PersonalWords;               // <- 기존 엔티티의 경로

import hi_light.level_test.repository.WordRepository;
import hi_light.level_test.entity.Word;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Random;
import java.util.Optional;

@Service
public class WordScrambleGameService {
    private final Map<String, WordScrambleGame> activeGames = new ConcurrentHashMap<>();
    // **멀티스레딩**: 게임 타이머 관리를 위한 스레드 풀
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    // 웹소켓 메시지를 보내기 위한 템플릿
    private final SimpMessagingTemplate messagingTemplate;

    // personal_words 폴더의 리포지토리를 주입받습니다.
    private final PersonalWordsRepository personalWordsRepository;
    private final WordRepository wordRepository;

    @Autowired // Spring이 SimpMessagingTemplate 객체를 자동으로 연결해줍니다.
    public WordScrambleGameService(SimpMessagingTemplate messagingTemplate, PersonalWordsRepository personalWordsRepository, WordRepository wordRepository) {
        this.messagingTemplate = messagingTemplate;
        this.personalWordsRepository = personalWordsRepository;
        this.wordRepository = wordRepository;
    }

    // 게임 시작 및 타이머 설정
    public void startGame(String gameId, List<Player> players) {
        String prompt = selectPromptWord(players); // 단어장에서 랜덤으로 제시어 선택
        // **문제 해결**: players 리스트를 WordScrambleGame 생성자에 전달합니다.
        WordScrambleGame game = new WordScrambleGame(gameId, players, prompt);
        activeGames.put(gameId, game);

        // **멀티스레딩**: 게임 타이머를 위한 별도의 스레드 시작
        // 모든 플레이어에게 제시어를 보냅니다. (웹소켓 사용)
        messagingTemplate.convertAndSend("/topic/game/" + gameId, "게임이 시작되었습니다! 제시어: " + prompt);

        // 60초 후 게임 종료
        scheduler.schedule(() -> endGame(gameId), 60, TimeUnit.SECONDS);
    }

    // 플레이어의 답안을 처리
    public void submitAnswer(String gameId, String playerId, String answer) {
        WordScrambleGame game = activeGames.get(gameId);
        if (game == null || game.isFinished()) return;

        // **문제 해결**: 동기화 범위를 game 객체 전체로 변경하여 안전성을 높입니다.
        synchronized (game) {
            // 정답을 맞힌 경우
            if (answer.equals(game.getPromptWord())) {
                if (!game.getSubmissionOrder().contains(playerId)) {
                    game.getSubmissionOrder().add(playerId);
                    int score = game.getTotalPlayers() - game.getSubmissionOrder().size() + 1;
                    game.getScores().put(playerId, score);
                    // **문제 해결**: 웹소켓 메시지 전송 코드를 활성화합니다.
                    messagingTemplate.convertAndSend("/topic/game/" + gameId, playerId + "님이 정답을 맞혔습니다!");
                }
            } else {
                // 틀렸을 경우: 점수 차감
                // 플레이어가 아직 점수를 받지 못했을 경우 초기값 0을 사용합니다.
                int currentScore = game.getScores().getOrDefault(playerId, 0);
                int penalty = game.getTotalPlayers() / 2;
                game.getScores().put(playerId, currentScore - penalty);
            }
        }
    }

    // **수정된 부분**: 게임 종료 후 최종 점수와 순위를 계산하여 프론트엔드로 전송합니다.
    public void endGame(String gameId) {
        WordScrambleGame game = activeGames.get(gameId);
        if (game == null || game.isFinished()) return;
        game.setFinished(true);

        // 정답을 맞히지 못한 플레이어는 0점 처리
        game.getPlayers().forEach(p -> {
            if (!game.getSubmissionOrder().contains(p.getId())) {
                game.getScores().put(p.getId(), 0);
            }
        });

        // 1. 최종 점수를 기준으로 플레이어 목록을 내림차순 정렬합니다.
        List<Map<String, Object>> finalScores = game.getPlayers().stream()
                .map(player -> {
                    Map<String, Object> playerScore = new ConcurrentHashMap<>();
                    playerScore.put("playerId", player.getId());
                    playerScore.put("nickname", player.getNickname());
                    playerScore.put("score", game.getScores().getOrDefault(player.getId(), 0));
                    return playerScore;
                })
                .sorted(Comparator.comparingInt((Map<String, Object> s) -> (Integer) s.get("score")).reversed())
                .collect(Collectors.toList());

        // 2. 최종 점수와 순위를 웹소켓을 통해 모든 플레이어에게 전송합니다.
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/result", finalScores);

        // 3. 게임방을 활성 게임 목록에서 제거합니다.
        activeGames.remove(gameId);
    }

    /**
     * 게임 참여자들의 개인 단어장을 합쳐 제시어를 선정합니다.
     * @param players 게임에 참여하는 플레이어 목록
     * @return 선정된 제시어
     */
    private String selectPromptWord(List<Player> players) {
        Set<String> combinedWordSet = new HashSet<>();

        // 1. 모든 플레이어의 개인 단어장을 취합
        players.forEach(player -> {
            // 주입받은 personalWordRepository를 사용하여 DB에서 단어장을 조회합니다.
            List<PersonalWords> personalWords = personalWordsRepository.findByUserId(player.getId());
            personalWords.forEach(word -> combinedWordSet.add(word.getWordEn()));
        });

        // 2. 취합된 단어장에서 랜덤으로 하나를 선택
        List<String> wordsList = new ArrayList<>(combinedWordSet);
        // **수정된 부분**: 개인 단어장이 모두 비어있을 경우
        if (wordsList.isEmpty()) {
            // 플레이어의 레벨을 가져옵니다. (모든 플레이어의 레벨은 동일합니다)
            String playerLevel = players.get(0).getRank();

            // 레벨에 맞는 단어 중 무작위 30개를 가져옵니다.
            List<hi_light.level_test.entity.Word> randomWords = wordRepository.findRandomWordsByLevel(playerLevel);

            // 만약 레벨별 단어장도 비어있을 경우, "기본단어"를 반환합니다.
            if (randomWords.isEmpty()) {
                return "게임을 할 단어가 없습니다.";
            }

            // 가져온 단어 목록에서 랜덤으로 하나를 선택합니다.
            Random random = new Random();
            return randomWords.get(random.nextInt(randomWords.size())).getWordEn();
        }

        // 3. 개인 단어장이 있을 경우, 그 단어장에서 랜덤으로 하나를 선택합니다.
        Random random = new Random();
        return wordsList.get(random.nextInt(wordsList.size()));
    }
}
