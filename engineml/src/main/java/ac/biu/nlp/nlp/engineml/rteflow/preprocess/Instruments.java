package ac.biu.nlp.nlp.engineml.rteflow.preprocess;
import eu.excitementproject.eop.common.representation.parse.BasicParser;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessor;
import eu.excitementproject.eop.lap.biu.en.coreference.CoreferenceResolver;
import eu.excitementproject.eop.lap.biu.en.ner.NamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.SentenceSplitter;


/**
 * An {@linkplain Instruments} object contain methods that
 * return the instruments, <B>not initialized</B>.
 * 
 * @author Asher Stern
 * @since Feb 26, 2011
 *
 * @param <I>
 * @param <S>
 */
public interface Instruments<I, S extends AbstractNode<I, S>>
{
	public BasicParser getParser();
	
	public NamedEntityRecognizer getNamedEntityRecognizer();
	
	public CoreferenceResolver<S> getCoreferenceResolver();
	
	public SentenceSplitter getSentenceSplitter();
	
	public TextPreprocessor getTextPreprocessor();
}
