//package hi_light.level_test.util;
//
//import hi_light.level_test.entity.Word;
//import org.apache.commons.csv.*;
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//
//public class CsvUtils {
//    public static List<Word> readWordsFromCsv(String filePath) throws IOException {
//        List<Word> words = new ArrayList<>();
//        try (Reader reader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8);
//             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
//            for (CSVRecord record : csvParser) {
//                Word word = new Word();
//                word.setWordEn(record.get("word_en"));
//                word.setKoreanMeaning(record.get("word_ko"));
//                word.setPartOfSpeech(record.get("part_of_speech"));
//                word.setLevel(record.get("level"));
//                words.add(word);
//            }
//        }
//        return words;
//    }
//}
