import org.clulab.odin.ExtractorEngine;
import org.clulab.odin.Mention;
import org.clulab.odin.TextBoundMention;
import org.clulab.processors.Document;
import org.clulab.processors.corenlp.CoreNLPProcessor;
import org.clulab.processors.Processor;
import org.clulab.processors.fastnlp.FastNLPProcessor;

import scala.collection.JavaConverters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    private static Collection<String>  getArguments(Mention mention)
    {
        return JavaConverters.mapAsJavaMap(mention.arguments()).keySet();
    }
    private static Optional<String> extractArgument(Mention mention, String arg)
    {
        if (mention.arguments().get(arg).isEmpty())
            return Optional.empty();
        Collection coll = JavaConverters.asJavaCollection(JavaConverters.mapAsJavaMap(mention.arguments()).get(arg));
        if (coll.size()==0)
            return Optional.empty();
        TextBoundMention tbm = (TextBoundMention)coll.toArray()[0];
        String[] tokens = tbm.document().sentences()[tbm.sentence()].words();
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i=tbm.tokenInterval().start(); i<tbm.tokenInterval().end(); i++)
        {
            if (!first)
                sb.append(' ');
            sb.append(tokens[i]);
            first = false;
        }
        return Optional.of(sb.toString());
    }
    public static void main(String[] args) {
        String dataset = "procedures";
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
//            System.out.println(line);
            doc = proc.annotate(line, true);
//            doc = proc.mkDocument(line, false);
//            proc.tagPartsOfSpeech(doc);
//            proc.lemmatize(doc);
//            proc.recognizeNamedEntities(doc);
//            doc.clear();
            Collection<Mention> mentions = JavaConverters.asJavaCollection(ee.extractFrom(doc));
            for (Mention mention : mentions) {
//                for(String arg: getArguments(mention))
//                {
//                    String txt = extractArgument(mention, arg).get();
//                    System.out.println(txt);
//                }
                System.out.println(mention.arguments().size());
                System.out.println(String.format("%s => %s : %s",
                        mention.foundBy(), mention.text(), mention.label()
                        ));
            }
        }
    }
}
