package lucene_1;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.StemmerOverrideFilterFactory;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
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


public class MainSearcher {


	@SuppressWarnings("deprecation")
	public static void main(String args[]) throws IOException, ParseException {

		Path path = Paths.get("./lucene-index/lucene_index"); //dove fare store del Lucene index
		Directory dir= FSDirectory.open(path);

		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);


		//1
		System.out.println("Prima query:");
		Query all = new MatchAllDocsQuery(); //mi aspetto tutti i match

		System.out.println("All Queries:");
		TopDocs hits = searcher.search(all, 10); 
		System.out.println(hits.scoreDocs.length);
		System.out.println("\r\n \r\n");


		//2
		System.out.println("Seconda query:");
		TermQuery q2= new TermQuery(new Term("titolo", "al")); //mi aspetto due match

		hits = searcher.search(q2, 10); 
		System.out.println(hits.scoreDocs.length);
		System.out.println(searcher.doc(hits.scoreDocs[0].doc).get("titolo")); 
		System.out.println(searcher.doc(hits.scoreDocs[1].doc).get("titolo"));
		System.out.println("\n");


		//3
		System.out.println("Terza query:");
		TermQuery q3= new TermQuery( new Term("contenuto", "StockX")); //sul titolo farei subito match
		hits = searcher.search(q3, 10); 
		if(hits.scoreDocs.length==0) {
			System.out.println("Mhh cerco simili");
			String g= q3.getTerm().text();
			hits= searcher.search(new TermQuery(new Term("contenuto", g.toLowerCase())), 10); //per avere match
			if(hits.scoreDocs.length!=0) {
				System.out.println(searcher.doc(hits.scoreDocs[0].doc).get("titolo"));
				System.out.println(searcher.doc(hits.scoreDocs[0].doc).get("contenuto"));
			}else{
				System.out.println("Nessun risultato per la query!");
			}
		}

		//4
		System.out.println("Quarta query:");
		PhraseQuery q4= new PhraseQuery(0, "contenuto",  "air", "mag"); //mi aspetto due match
		//PhraseQuery q4= new PhraseQuery(0, "contenuto",  "Nike", "air", "Mag"); non mi aspetterei nessun match

		hits = searcher.search(q4, 10); 
		System.out.println(hits.scoreDocs.length);
		System.out.println(searcher.doc(hits.scoreDocs[0].doc).get("titolo")); //Le sneakers più costose di sempre su StockX
		System.out.println(searcher.doc(hits.scoreDocs[1].doc).get("titolo")); //Ritorno al futuro

		System.out.println("\n");


		//5
		System.out.println("Quinta query:");
		TermQuery tq5= new TermQuery(new Term("contenuto", "da"));
		PhraseQuery pq5= new PhraseQuery(0, "contenuto", "air", "mag");
		BooleanQuery bq5 = new BooleanQuery.Builder()
				.add(new BooleanClause(tq5, BooleanClause.Occur.MUST_NOT)) //"da" tanto non l'ho indicizzato (-)
				.add(new BooleanClause(pq5, BooleanClause.Occur.MUST)) //+
				.add(new BooleanClause(new TermQuery(new Term("titolo", "StockX")), BooleanClause.Occur.SHOULD))
				.build();

		hits = searcher.search(bq5, 10); 
		System.out.println(hits.scoreDocs.length);
		System.out.println(searcher.doc(hits.scoreDocs[0].doc).get("titolo")); //Le sneakers più costose di sempre su StockX
		System.out.println(searcher.doc(hits.scoreDocs[1].doc).get("titolo")); //Ritorno al futuro
		
		System.out.println("\n");


		//6 Boolean query
		System.out.println("Sesta query:");
		TermQuery tq6= new TermQuery(new Term("contenuto", "fantascienza")); //
		BooleanQuery bq6 = new BooleanQuery.Builder()
				.add(new BooleanClause(tq6, BooleanClause.Occur.MUST)) 
				.build();

		hits = searcher.search(bq6, 10); 
		System.out.println(hits.scoreDocs.length);
		System.out.println(searcher.doc(hits.scoreDocs[0].doc).get("titolo")); //Ritorno al futuro
		System.out.println(searcher.doc(hits.scoreDocs[1].doc).get("titolo")); //Viaggi nel tempo-ritornare al passato o andare nel futuro
		
		System.out.println("\n");


		//7
		System.out.println("Settima query:");
		TermQuery tq7= new TermQuery(new Term("contenuto", "da"));
		PhraseQuery pq7= new PhraseQuery(0, "contenuto", "Air", "Mag");
		BooleanQuery bq7 = new BooleanQuery.Builder()
				.add(new BooleanClause(tq7, BooleanClause.Occur.MUST_NOT)) //"da" tanto non l'ho indicizzato (-)
				.add(new BooleanClause(pq7, BooleanClause.Occur.MUST)) //+
				.add(new BooleanClause(new TermQuery(new Term("titolo", "StockX")), BooleanClause.Occur.SHOULD))
				.build();

		hits = searcher.search(bq7, 10);
		if(hits.scoreDocs.length==0) { //problema o con MUST o con MUST_NOT provo sulla clusola del MUST a rifare la query con tutte le parole minuscole?
			Query q;
			List<String> fixList= new ArrayList<String>();
			for(BooleanClause b: bq7.clauses()) {
				if(b.getOccur().toString().equals("+")) {
					q= b.getQuery();
					Term[] terms = ((PhraseQuery) q).getTerms();
					fixList.add(terms[0].text().toLowerCase()); // "air"
					fixList.add(terms[1].text().toLowerCase()); //"mag"
					break;
				}
			}

			Builder builder = new BooleanQuery.Builder();
			for(BooleanClause b: bq7.clauses()) {
				if(b.getOccur().toString().equals("+")) {
					if(b.getQuery().getClass().toString().equals(PhraseQuery.class.toString())) { 
						PhraseQuery npq7= new PhraseQuery(((PhraseQuery) b.getQuery()).getSlop(), "contenuto", fixList.get(0),  fixList.get(1));
						builder.add(new BooleanClause(npq7, BooleanClause.Occur.MUST));
					}
				}
				else {
					builder.add(b);
				}
				//l'idea è che si può generalizzare per tutti i tipi di query nel senso che io non so quando uno esegue una query con l'occur MUST
				//che tipo di query sia quindi a cascata if-esle per sapere bene il tipo dinamico della query, cioè lavoro manipolando la query se non ho match
				//per vedere se ho match
				//				Query j = null;
				//				if(b.getQuery().getClass().toString().equals(PhraseQuery.class.toString())) {
				//					j= new PhraseQuery(0, "contenuto", "Air", "Mag");
				//					((PhraseQuery) j).getTerms();
				//				}else if(b.getQuery().getClass().toString().equals(TermQuery.class.toString())) {
				//					j= new TermQuery(new Term("titolo", "StockX"));
				//				}

			}
			BooleanQuery newBooleanQuery= builder.build();
			hits = searcher.search(newBooleanQuery, 10);
			System.out.println(hits.scoreDocs.length);
			System.out.println(searcher.doc(hits.scoreDocs[0].doc).get("titolo"));
			System.out.println(searcher.doc(hits.scoreDocs[1].doc).get("titolo")); 
		}
		else {
			System.out.println(hits.scoreDocs.length);
			System.out.println(searcher.doc(hits.scoreDocs[0].doc).get("titolo"));
			System.out.println(searcher.doc(hits.scoreDocs[1].doc).get("titolo"));
		}
		
		System.out.println("\n");


		//8
		System.out.println("Ottava query:");
		QueryParser parser = new QueryParser("contenuto", new WhitespaceAnalyzer());
		Query q8 = parser.parse("+film +marty +mcfly indossa"); //se lo srivo Marty McFly l'idea è che lo posso matchare mettendo tutto minuscolo
		//ma il contrario non l'ho posso fare cioé ho indicizzato Marty McFly e scrivo marty mcfly quali lettere devo mettere in maiuscolo?
		hits = searcher. search(q8, 10);
		System.out.println(hits.scoreDocs.length);
		System.out.println(searcher.doc(hits.scoreDocs[0].doc).get("titolo")); //Ritorno al futuro (score più alto)
		System.out.println(searcher.doc(hits.scoreDocs[1].doc).get("titolo")); //Le sneakers più costose di sempre su StockX
		
		System.out.println("\n");
		
		
		
	}

}



