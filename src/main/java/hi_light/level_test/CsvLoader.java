//package hi_light.level_test;
//
//import hi_light.level_test.entity.Word;
//import hi_light.level_test.repository.WordRepository;
//import hi_light.level_test.util.CsvUtils;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class CsvLoader implements CommandLineRunner {
//
//    private final WordRepository wordRepository;
//
//    public CsvLoader(WordRepository wordRepository) {
//        this.wordRepository = wordRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        String[] levels = {"A1", "A2", "B1", "B2", "C1", "C2"};
//        for (String level : levels) {
//            String path = "C:/Users/kyj65/OneDrive/Desktop/level_words/" + level + "_words.csv";
//            List<Word> words = CsvUtils.readWordsFromCsv(path);
//
//            // 한 번에 100~500개씩 나눠서 저장
//            int batchSize = 80;
//            for (int i = 0; i < words.size(); i += batchSize) {
//                int end = Math.min(i + batchSize, words.size());
//                wordRepository.saveAll(words.subList(i, end));
//            }
//
//            System.out.println(level + " 단어 " + words.size() + "개 저장 완료");
//        }
//    }
//}
