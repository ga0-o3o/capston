package hi_light.personal_words.service;

import hi_light.personal_words.dto.WordResponse;

import hi_light.personal_words.dto.WordRequest;
import hi_light.personal_words.entity.PersonalWords;
import hi_light.personal_words.repository.PersonalWordsRepository;
import hi_light.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonalWordsService {

    private final PersonalWordsRepository personalWordsRepository;
    private final UserRepository userRepository;

    @Transactional
    public PersonalWords addWord(WordRequest request) { // 영단어를 db에 추가하는 함수
        //유효성 검사: WordRequest의 userId가 실제 DB에 존재하는지 확인
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + request.getUserId() + " not found."));

        PersonalWords newWord = new PersonalWords();
        newWord.setUserId(request.getUserId());
        newWord.setWordEn(request.getWordEn());
        newWord.setKoreanMeaning(request.getKoreanMeaning());
        newWord.setRegisteredAt(LocalDate.now());
        // 등록 후 첫 복습일은 1일 후로 설정
        newWord.setNextReviewAt(LocalDate.now().plusDays(1));
        newWord.setReviewCount(0);

        return personalWordsRepository.save(newWord);
    }

    @Transactional
    public PersonalWords updateNextReviewDate(Long wordId, String userId) { // (복습완료버튼을 눌렀을 때)복습일을 업데이트 하는 함수
        //userId 유효성 검사 추가
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));

        Optional<PersonalWords> optionalWord = personalWordsRepository.findById(wordId);

        if (optionalWord.isPresent()) {
            PersonalWords word = optionalWord.get();

            if (!word.getUserId().equals(userId)) {
                throw new IllegalStateException("You can only update your own words.");
            }

            LocalDate today = LocalDate.now();
            LocalDate currentReviewDate = word.getNextReviewAt();

            // 복습일이 지났다면 오늘을 기준으로, 아니면 기존 복습일을 기준으로 다음 복습일 계산
            LocalDate baseDate = currentReviewDate.isBefore(today) ? today : currentReviewDate;

            // 에빙하우스 망각 곡선에 따른 복습 주기 계산
            int reviewCount = word.getReviewCount();
            long daysToAdd;

            switch (reviewCount) {
                case 0: // 첫 복습 완료
                    daysToAdd = 2;
                    break;
                case 1: // 2차 복습 완료
                    daysToAdd = 4;
                    break;
                case 2: // 3차 복습 완료
                    daysToAdd = 7;
                    break;
                case 3: // 4차 복습 완료
                    daysToAdd = 15;
                    break;
                case 4: // 5차 복습 완료
                    daysToAdd = 30;
                    break;
                default: // 6차 복습 이후
                    daysToAdd = 60;
                    break;
            }

            LocalDate nextReviewDate = baseDate.plusDays(daysToAdd);

            word.setNextReviewAt(nextReviewDate);
            word.setReviewCount(reviewCount + 1); // 복습 횟수 1 증가
            return personalWordsRepository.save(word);
        } else {
            throw new IllegalArgumentException("Word with ID " + wordId + " not found.");
        }
    }

    @Transactional
    public PersonalWords updateWord(Long wordId, WordRequest request) { // 등록된 단어를 수정하는 함수
        //userId 유효성 검사 추가
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + request.getUserId() + " not found."));

        // 단어의 존재 유무를 확인하고, 없으면 예외 발생
        PersonalWords word = personalWordsRepository.findById(wordId)
                .orElseThrow(() -> new IllegalArgumentException("Word with ID " + wordId + " not found."));

        if (!word.getUserId().equals(request.getUserId())) {
            throw new IllegalStateException("You can only update your own words.");
        }
        if (request.getWordEn() != null) {
            word.setWordEn(request.getWordEn());
        }
        if (request.getKoreanMeaning() != null) {
            word.setKoreanMeaning(request.getKoreanMeaning());
        }
        return personalWordsRepository.save(word);
    }

    @Transactional
    public void deleteWord(Long wordId, String userId) { // 단어장의 영단어를 삭제하는 함수
        // ★ userId 유효성 검사 추가
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));

        // 단어의 존재 유무를 확인하고, 없으면 예외 발생
        PersonalWords word = personalWordsRepository.findById(wordId)
                .orElseThrow(() -> new IllegalArgumentException("Word with ID " + wordId + " not found."));

        if (!word.getUserId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own words.");
        }
        personalWordsRepository.delete(word);
    }

    //  특정 사용자가 자신의 단어장 목록을 볼 때 호출되는 함수. 목록 형태로 반환
    public List<WordResponse> getWordsByUserId(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));

        List<PersonalWords> personalWordsList = personalWordsRepository.findByUserId(userId);
        List<WordResponse> responseList = new ArrayList<>();

        int displayId = 1;
        for (PersonalWords word : personalWordsList) {
            // Builder 패턴을 사용하여 객체 생성
            responseList.add(WordResponse.builder()
                    .id(word.getId())
                    .displayId(displayId++) // 순차 번호 할당
                    .userId(word.getUserId())
                    .wordEn(word.getWordEn())
                    .koreanMeaning(word.getKoreanMeaning())
                    .registeredAt(word.getRegisteredAt())
                    .nextReviewAt(word.getNextReviewAt())
                    .build()
            );
        }

        return responseList;
    }
}
