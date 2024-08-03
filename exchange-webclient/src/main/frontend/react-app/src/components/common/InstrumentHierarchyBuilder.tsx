import { useEffect, useState } from "react";
import { fetchInstrumentHierarchy } from "../../common/rest-client";
import { TreeNode } from "../../common/types";
import TreeBuilder from "./TreeBuilder";
import { InstrumentHierarchy } from "../../common/types";

interface Props {
  onItemSelect: (node: TreeNode) => void;
}

function InstrumentHierarchyBuilder({ onItemSelect }: Props) {
  const [rootNode, setRootNode] = useState<TreeNode | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const getInstrumentHierarchy = async () => {
      try {
        const hierarchy: InstrumentHierarchy = await fetchInstrumentHierarchy();
        setRootNode(hierarchy.instrumentTree.rootNode);
      } catch (error: any) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    getInstrumentHierarchy();
  }, []);

  return (
    <div>
      <h3>Instrument Selection</h3>
      {rootNode ? (
        <TreeBuilder data={rootNode.children ?? []} onItemSelect={onItemSelect} />
      ) : (
        <p>No data available</p>
      )}
    </div>
  );
}

export default InstrumentHierarchyBuilder;
