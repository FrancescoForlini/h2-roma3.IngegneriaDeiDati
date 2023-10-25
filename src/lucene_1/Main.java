package lucene_1;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


import org.apache.lucene.analysis.*;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.it.ItalianLightStemFilterFactory;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.StemmerOverrideFilterFactory;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.IOUtils;



public class Main {




	public static void main(String args[]) throws IOException {



		Path path = Paths.get("./lucene-index/lucene_index"); //dove fare store del Lucene index
		Directory dir= FSDirectory.open(path);

		Map <String, Analyzer> perFieldAnalyzers1 = new HashMap<>();
		
		CharArraySet stopWords = new CharArraySet(Arrays.asList("in", "dei", "di", "a", "da", "in", "con", "su", "per", "tra", "i", "del", "quello", "che", "infatti", "quindi",
				"nel", "le", "della", "ai", "ed"), true); 
		Analyzer analyzer11= new StandardAnalyzer(stopWords);
		
		Analyzer analyzer11alter = CustomAnalyzer.builder()
				.withTokenizer(StandardTokenizerFactory.class) //
				.addTokenFilter(LowerCaseFilterFactory.class)
				.addTokenFilter(StopFilterFactory.class, "words", "stopping.txt")
				.build();
		
		//.addTokenFilter(WordDelimiterGraphFilterFactory.class) StockX me lo mette Stock X
		//.addTokenFilter(LowercaseFilterFactory.class) StockX me lo mette stockx

		

		perFieldAnalyzers1.put("titolo", new WhitespaceAnalyzer()); //analyzer del titolo
		perFieldAnalyzers1.put("contenuto", analyzer11alter); //potevo mettere pure analyzer11
		Analyzer analyzer1 = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), perFieldAnalyzers1);


		IndexWriterConfig config1 = new IndexWriterConfig(analyzer1); //ha bisogno di un analyzer



		Codec codec= new SimpleTextCodec();
		if (codec != null) {
			config1.setCodec(codec);
		}
		IndexWriter writer= new IndexWriter(dir, config1);


		//		2
		//		IndexWriterConfig configg = new IndexWriterConfig();
		//		configg.setCodec(new SimpleTextCodec()); //.cfe, .cfs(7kb), .si




		Document doc1f= new Document();
		doc1f.add(new TextField("titolo", "Ritorno al futuro", Field.Store.YES));
		doc1f.add(new TextField("contenuto", "Ritorno al futuro\" (in inglese \"Back to the Future\") è un famoso film\r\n"
				+ " di fantascienza: diretto da Robert Zemeckis e scritto da Robert Zemeckis e Bob Gale.\r\n"
				+ " Il film è stato distribuito per la prima volta nel 1985 ed è diventato un classico del cinema. \r\n "
				+ "La trama del film è incentrata sul viaggio nel tempo e su un adolescente di nome Marty McFly, \r\n"
				+ "interpretato da Michael J. Fox, e il suo eccentrico amico dottor Emmett \"Doc\" Brown, interpretato da Christopher Lloyd. \r\n"
				+ "\r\n" + "\r\n" + 
				"Il film parla di Marty McFly, un normale adolescente che fa amicizia con il suo eccentrico vicino, \r\n"
				+ "il dottor Emmett Brown. Doc Brown ha creato una macchina del tempo su base DeLorean, che funziona grazie \r\n"
				+ "a una sorgente di energia chiamata plutonio. Per una serie di vicessitudini Marty si ritroverà accidentalmente nel 1955. \r\n"
				+ "Inseguito da criminali cercherà di tornare indietro nel suo tempo per evitare che un evento possa cambiare il corso della storia. \r\n"
				+ "Inoltre, imbattendosi nella versione più giovane dei suoi genitori, con mille peripezie, dovrà assicurarsi che i loro genitori \r\n"
				+ "si innamorino per garantire la loro esistenza futura.\r\n" + 
				"\r\n" + 
				"Il film è noto per la sua miscela di commedia, avventura e fantascienza, nonché per due iconici oggetti:il primo, \r\n"
				+ "la famosa macchina del tempo DeLorean utilizzata per il viaggio nel tempo e il secondo, le celebri Nike Air Mag \r\n"
				+ "indossate da Marty McFly, scarpe con un sistema di auto-allacciamento del futuro che sono diventate \r\n"
				+ "sneaker di culto tra gli appassionati (uscite davvero nel 2016). \"Ritorno al futuro\" ha avuto due seguiti \r\n"
				+ "(\"Ritorno al futuro - Parte II\" e \"Ritorno al futuro - Parte III\") e ha avuto un grande impatto \r\n"
				+ "sulla cultura popolare. È amato per il suo umorismo intelligente, i personaggi memorabili e la storia \r\n"
				+ "coinvolgente che esplora i paradossi temporali.", Field.Store.YES));


		Document doc2vt= new Document();
		doc2vt.add(new TextField("titolo", "Viaggi nel tempo-ritornare al passato o andare nel futuro", Field.Store.YES));
		doc2vt.add(new TextField("contenuto", "Il concetto di viaggio nel tempo è un tema affascinante e ricorrente nella letteratura \r\n"
				+ "e nella narrativa scientifica. Tuttavia, è importante sottolineare che al momento attuale, il viaggio nel tempo, \r\n"
				+ "nel senso tradizionale in cui spesso viene rappresentato nei film e nella fantascienza, rimane una speculazione \r\n"
				+ "teorica e non è stato dimostrato scientificamente. Inoltre, il viaggio nel tempo potrebbe comportare problemi teorici, \r\n"
				+ "come il \"paradosso del nonno\", che mettono in dubbio la sua fattibilità.\r\n" + 
				"\r\n" + 
				"Ecco alcune considerazioni sul viaggio nel tempo nel passato e nel futuro:\r\n" + 
				"\r\n" +
				"viaggio nel passato: Il concetto di viaggio nel passato solleva questioni complesse. \r\n"
				+ "Se fosse possibile tornare indietro nel tempo ed influenzare eventi passati, ciò  potrebbe portare a contraddizioni \r\n"
				+ "e a paradossi, come il \"paradosso del nonno\", in cui si potrebbe impedire la propria esistenza stessa. \r\n"
				+ "Tali problemi rendono il viaggio nel passato una questione teorica complicata.\r\n" + 
				"\r\n" + 
				"viaggio nel futuro: Sebbene il viaggio nel futuro sia anch'esso un concetto speculativo, \r\n"
				+ "è meno soggetto ai problemi del paradosso rispetto al viaggio nel passato. La teoria della relatività di Einstein \r\n"
				+ "suggerisce che il viaggio nel futuro è possibile in alcune circostanze. Ad esempio, viaggiare a velocità prossime \r\n"
				+ "a quella della luce o vicino a oggetti con forti campi gravitazionali potrebbe far scorrere il tempo in modo diverso \r\n"
				+ "per l'osservatore in movimento, consentendo un \"viaggio\" nel futuro.\r\n" +
				"\r\n" +
				"In sintesi, il viaggio nel tempo è principalmente un argomento di speculazione teorica e narrativa piuttosto \r\n"
				+ "che una realtà scientifica. Sebbene sia un argomento affascinante, rimane al di fuori delle possibilità pratiche \r\n"
				+ "dell'attuale comprensione scientifica. Le teorie e le rappresentazioni del viaggio nel tempo sono spesso utilizzate \r\n"
				+ "come dispositivi narrativi nella letteratura, nel cinema come in \"Ritorno al Futuro\" e nella fantascienza, per esplorare \r\n"
				+ "temi intriganti e complessi legati al tempo, al destino e alla causalità", Field.Store.YES));

		/*
		 * Document doc3s= new Document();

		doc3s.add(new TextField("titolo", "Le sneakers più costose di sempre su StockX", Field.Store.YES));
		doc3s.add(new TextField("contenuto", "StockX è una piattaforma di e-commerce specializzata nella compravendita di sneakers \r\n"
				+ "(scarpe da ginnastica), streetwear, borse, orologi e altri articoli di moda e lifestyle. Fondata nel 2015 a Detroit, \r\n"
				+ "Michigan, StockX ha guadagnato popolarità come uno dei principali mercati online per articoli di moda di alta qualità \r\n"
				+ "e di edizione limitata. \r\n" +
				"Per tali motivi per alcuni pezzi i prezzi possono essere davvero elevati. \r\n" +
				"\r\n" +
				"Ecco alcune delle sneaker più costose su StockX in questo momento: \r\n" +
				"\r\n" +
				"Nike Air Mag Back to the Future (2016)\r\n" + 
				"Prezzo Medio: $41.429\r\n" +  
				"Cinque anni dopo la vendita all'asta della prima serie delle celebri Nike Air Mag, Nike ha trasformato la funzionalità \r\n"
				+ "vista sul grande schermo in una realt� di design grazie a Nike Adapt, l'innovativa tecnologia di allacciatura \r\n"
				+ "automatica. L'idea di una Nike con sistema di auto-allacciamento si è vista per la prima volta in Ritorno al Futuro \r\n"
				+ ", e con questa tecnologia finalmente a disposizione i fortunati che riuscirono a vincere la raffle ne ottennero un \r\n"
				+ "paio ancora più simile a quello indossato nel film da Marty McFly indosso .\r\n" +
				"\r\n" +
				"Nike SB Dunk Low Staple Pigeon NYC\r\n" + 
				"Prezzo Medio: $33.400\r\n" +  
				"Probabilmente la release che ha esposto il mondo all’isteria per il modello Nike SB Dunk, \r\n"
				+ "la Nike SB Dunk Low Staple Pigeon NYC di Jeff Staple era ed è ancora una delle sneakers più costose al mondo. \r\n"
				+ "La folla si accalcava fuori dal Reed Space di New York per ottenerne un paio nel 2005, quando il valore di \r\n"
				+ "rivendita delle Dunk era di poche migliaia di dollari. È impossibile immaginare cosa avrebbe fatto quella folla \r\n"
				+ "se avesse conosciuto in anticipo il prezzo che le Pigeon avrebbero raggiunto sedici anni più tardi.\r\n" +
				"\r\n" +  
				"Nike SB Dunk Low Yellow Lobster\r\n" + 
				"Prezzo Medio: $30.453\r\n" +
				"Nel luglio del 2009 si diffuse la notizia che un pescatore aveva catturato una rarissima aragosta gialla(proabbailità=1/1000000). \r\n"
				+ "Come risposta a questo evento speciale, Nike SB e Concepts, famosa boutique di Boston, hanno regalato 36 \r\n"
				+ "paia di Nike SB Dunk Low Yellow Lobster ad amici e familiari appena prima della release delle Nike SC Dunk \r\n"
				+ "Low Blue Lobster.", null));



		 */


		//doc3
		String percorsoFile = "C:\\Users\\H\\Desktop\\Magistrale_II_anno_primo semestre\\Ingegneria dei dati\\Secondo homework\\lucene_java\\file_txt\\Le sneakers più costose di sempre su StockX.txt";
		StringBuilder contenuto3 = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new FileReader(percorsoFile))) {
			String linea;
			while ((linea = reader.readLine()) != null) {
				contenuto3.append(linea).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String contenuto = contenuto3.toString();
		System.out.println(contenuto+ "\r\n");


		Document doc3s= new Document();
		doc3s.add(new TextField("titolo", "Le sneakers più costose di sempre su StockX", Field.Store.YES));
		doc3s.add(new TextField("contenuto", contenuto, Field.Store.YES));

		writer.deleteAll();
		writer.addDocument(doc1f);
		writer.addDocument(doc2vt); 
		writer.addDocument(doc3s);
		writer.commit();

		writer.close();
		dir.close();
	}



}