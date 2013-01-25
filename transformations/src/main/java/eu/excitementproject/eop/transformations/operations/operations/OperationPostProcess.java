package eu.excitementproject.eop.transformations.operations.operations;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.ContentAncestorSetter;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.OriginalInfoTraceSetter;

/**
 * Makes some post-processing on the tree generated by subclasses of
 * {@link GenerationOperation} that have {@link ExtendedNode} as
 * their generic parameter, for example {@link GenerationOperationForExtendedNode}.
 * The post-processing currently includes tracing of original node, and content-ancestor setting.
 * 
 * @author Asher Stern
 * @since Dec 25, 2011
 *
 */
public class OperationPostProcess
{
	public OperationPostProcess(
			GenerationOperation<ExtendedInfo, ExtendedNode> operation) throws OperationException
	{
		super();
		this.operation = operation;
		
		this.generatedTree = operation.getGeneratedTree();
		this.mapOriginalToGenerated = operation.getMapOriginalToGenerated();
		this.affectedNodes = operation.getAffectedNodes();
	}
	
	public void postProcess() throws TeEngineMlException
	{
		if (Constants.TRACE_ORIGINAL_NODES)
		{
			postProcessSetTrace();
		}
		if (Constants.USE_ADVANCED_EQUALITIES)
		{
			postProcessContentAncestor();
		}
		postProcessDone = true;
	}
	
	public ExtendedNode getGeneratedTree() throws TeEngineMlException
	{
		if (!postProcessDone) throw new TeEngineMlException("Post process not done!");
		return generatedTree;
	}

	public ValueSetMap<ExtendedNode, ExtendedNode> getMapOriginalToGenerated() throws TeEngineMlException
	{
		if (!postProcessDone) throw new TeEngineMlException("Post process not done!");
		return mapOriginalToGenerated;
	}

	public Set<ExtendedNode> getAffectedNodes() throws TeEngineMlException
	{
		if (!postProcessDone) throw new TeEngineMlException("Post process not done!");
		return affectedNodes;
	}

	private void postProcessContentAncestor() throws TeEngineMlException
	{
		ContentAncestorSetter ancestorSetter = new ContentAncestorSetter(generatedTree);
		ancestorSetter.generate();
		BidirectionalMap<ExtendedNode, ExtendedNode> mapping = ancestorSetter.getNodesMap();
		ExtendedNode newTree = mapping.leftGet(generatedTree);
		Set<ExtendedNode> newAffectedNodes = generateMappedSet(affectedNodes, mapping);
		ValueSetMap<ExtendedNode, ExtendedNode> newMapOriginalToGenerated = generateMapWithMappedValues(mapping,mapOriginalToGenerated);
		
		generatedTree = newTree;
		affectedNodes = newAffectedNodes;
		mapOriginalToGenerated = newMapOriginalToGenerated;
	}
	
	private void postProcessSetTrace() throws TeEngineMlException
	{
		OriginalInfoTraceSetter setter = new OriginalInfoTraceSetter(generatedTree, mapOriginalToGenerated);
		setter.set();
		ExtendedNode newTree = setter.getNewGeneratedTree();
		BidirectionalMap<ExtendedNode, ExtendedNode> mapOfSetter = setter.getMapInputToNew();
		Set<ExtendedNode> newAffectedNodes = generateMappedSet(affectedNodes, mapOfSetter);
		ValueSetMap<ExtendedNode, ExtendedNode> newMapOriginalToGenerated = generateMapWithMappedValues(mapOfSetter,mapOriginalToGenerated);
		
		generatedTree = newTree;
		affectedNodes = newAffectedNodes;
		mapOriginalToGenerated = newMapOriginalToGenerated;
	}
	
	private static <T> Set<T> generateMappedSet(Set<T> originalSet, BidirectionalMap<T, T> mapOrigToGenerated)
	{
		Set<T> ret = new LinkedHashSet<T>();
		for (T orig : originalSet)
		{
			ret.add(
					mapOrigToGenerated.leftGet(orig)
			);
		}
		return ret;
	}
	
	private static <T> ValueSetMap<T, T> generateMapWithMappedValues(BidirectionalMap<T, T> bidiMap, ValueSetMap<T, T> originalValueSetMap)
	{
		ValueSetMap<T, T> ret = new SimpleValueSetMap<T, T>();
		for (T element : originalValueSetMap.keySet())
		{
			for (T mappedInOriginal : originalValueSetMap.get(element))
			{
				ret.put(element, bidiMap.leftGet(mappedInOriginal));
			}
		}
		return ret;
	}


	@SuppressWarnings("unused")
	private GenerationOperation<ExtendedInfo, ExtendedNode> operation;
	
	private ExtendedNode generatedTree = null;
	private ValueSetMap<ExtendedNode, ExtendedNode> mapOriginalToGenerated = null;
	private Set<ExtendedNode> affectedNodes = null;
	
	private boolean postProcessDone = false;
}