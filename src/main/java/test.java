import org.clulab.odin.ExtractorEngine;
import org.clulab.odin.Mention;
import org.clulab.processors.Document;
import org.clulab.processors.corenlp.CoreNLPProcessor;
import org.clulab.processors.Processor;
import org.clulab.processors.fastnlp.FastNLPProcessor;

import scala.collection.JavaConversions;
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
        String text = readFile(String.format("data/%s.txt", dataset)).orElse("");;
        String rules = readFile(String.format("data/%s.yaml", dataset)).orElse("");
        if (rules.equals(""))
            return;
        ExtractorEngine ee = ExtractorEngine.fromRules(rules);
        Processor proc = new CoreNLPProcessor(false, false,1,100);
//        Processor proc = new FastNLPProcessor(false, false,0);
//        Document doc = proc.mkDocument(text,  false);
        Document doc = null;
        for (String line:  text.split("\n")) {
            line = line.trim();
            if (line.equals(""))
                continue;
            if (line.charAt(0) == '#')
                continue;
            System.out.println(line);
            doc = proc.annotate(line, false);
            Collection<Mention> mentions = JavaConversions.asJavaCollection(ee.extractFrom(doc));
            for (Mention mention : mentions) {
                System.out.println(mention.text());
                System.out.println(mention.foundBy());
                System.out.println(mention.label());
            }
        }
    }
}
