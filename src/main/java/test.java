import org.clulab.odin.ExtractorEngine;
import org.clulab.odin.Mention;
import org.clulab.processors.Document;
import org.clulab.processors.corenlp.CoreNLPProcessor;
import org.clulab.processors.Processor;
import org.clulab.processors.fastnlp.FastNLPProcessor;

import scala.collection.JavaConverters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;

public class test {

    private static Optional<String> readFile(String filename)
    {
        try {
            return Optional.of(new String(Files.readAllBytes(Paths.get(filename))));
        } catch (IOException ex) {
            System.err.println(ex.toString());
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
        String dataset = "sample";
        String text = readFile(String.format("data/%s.txt", dataset)).orElse("");
        String rules = readFile(String.format("data/%s.yaml", dataset)).orElse("");
        if (rules.equals(""))
            return;
        ExtractorEngine ee = ExtractorEngine.fromRules(rules);
        Processor proc = new CoreNLPProcessor(true, true, true, 0, 100);
//        Processor proc = new FastNLPProcessor(false, false, true,0);
        Document doc;
        for (String line:  text.split("\n")) {
            line = line.trim();
            if (line.equals(""))
                continue;
            if (line.charAt(0) == '#')
                continue;
            System.out.println(line);
            doc = proc.annotate(line, false);
//            doc = proc.mkDocument(line, false);
//            proc.tagPartsOfSpeech(doc);
//            proc.lemmatize(doc);
//            proc.recognizeNamedEntities(doc);
//            doc.clear();
            Collection<Mention> mentions = JavaConverters.asJavaCollection(ee.extractFrom(doc));
            for (Mention mention : mentions) {
                System.out.println(mention.text());
                System.out.println(mention.foundBy());
                System.out.println(mention.label());
            }
        }
    }
}
