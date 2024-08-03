import React, { useEffect, useState } from "react";
import { TreeNode } from "../../common/types";

type TreeNodeProps = {
  node: TreeNode;
  onItemSelect: (node: TreeNode) => void;
};

const TreeNodeComponent: React.FC<TreeNodeProps> = ({ node, onItemSelect }) => {
  const [expanded, setExpanded] = useState(false);

  const handleToggle = () => {
    setExpanded(!expanded);
  };

  const handleSelect = () => {
    if (!node.children || node.children.length === 0) {
      onItemSelect(node);
    }
  };

  return (
    <div style={{ marginLeft: 20 }}>
      <div>
        {node.children && node.children.length > 0 && (
          <button onClick={handleToggle}>{expanded ? "-" : "+"}</button>
        )}
        <span
          onClick={handleSelect}
          style={{
            cursor: node.children && node.children.length > 0 ? "default" : "pointer",
            color: node.children && node.children.length > 0 ? "black" : "blue",
          }}
        >
          {node.name}
        </span>
      </div>
      {expanded &&
        node.children &&
        node.children.map((child) => (
          <TreeNodeComponent key={child.id} node={child} onItemSelect={onItemSelect} />
        ))}
    </div>
  );
};

type TreeProps = {
  data?: TreeNode[];
  onItemSelect: (node: TreeNode) => void;
};

function TreeBuilder({ data, onItemSelect }: TreeProps) {
  return (
    <div>
      {data?.map((node) => (
        <TreeNodeComponent key={node.id} node={node} onItemSelect={onItemSelect} />
      ))}
    </div>
  );
}

export default TreeBuilder;
