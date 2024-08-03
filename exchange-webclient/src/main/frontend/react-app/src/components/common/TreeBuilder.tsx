import React, { useEffect, useState } from "react";
import { TreeNode } from "../../common/types";

interface TreeNodeProps {
  node: TreeNode;
  onItemSelect: (node: any) => void;
}

const TreeNodeComponent: React.FC<TreeNodeProps> = ({ node, onItemSelect }) => {
  const [expanded, setExpanded] = useState(false);

  const handleToggle = () => {
    setExpanded((prev) => !prev);
  };

  const handleSelect = () => {
    onItemSelect(node);
  };

  const hasChildren = node.children && node.children.length > 0;

  return (
    <div className="tree-node-container">
      <div className="d-flex align-items-center">
        {hasChildren && (
          <button
            onClick={handleToggle}
            className={`btn btn-sm ${expanded ? "btn-outline-danger" : "btn-outline-success"} me-2`}
          >
            {expanded ? "-" : "+"}
          </button>
        )}
        <span
          onClick={hasChildren ? () => {} : handleSelect}
          className={`tree-node-name ${hasChildren ? "text-dark" : "text-primary"}`}
          style={{ cursor: hasChildren ? "default" : "pointer" }}
        >
          {hasChildren ? (
            node.name
          ) : (
            <>
              <span style={{ color: "black" }}>{"- "}</span>
              {node.name}
            </>
          )}
        </span>
      </div>
      {expanded && hasChildren && (
        <div className="tree-node-children ms-3">
          {node.children!.map((child: TreeNode) => (
            <TreeNodeComponent key={child.id} node={child} onItemSelect={onItemSelect} />
          ))}
        </div>
      )}
    </div>
  );
};

type TreeProps = {
  nodes?: TreeNode[];
  onItemSelect: (node: TreeNode) => void;
};

function TreeBuilder({ nodes, onItemSelect }: TreeProps) {
  return (
    <div>
      {nodes?.map((node) => (
        <TreeNodeComponent key={node.id} node={node} onItemSelect={onItemSelect} />
      ))}
    </div>
  );
}

export default TreeBuilder;
