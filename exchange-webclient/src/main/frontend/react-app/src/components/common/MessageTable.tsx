import { useState } from "react";
import { Message } from "../../common/types";

interface Props {
  items: Message[];
  heading: string;
  columnHeaders: string[];
  tableExtractor: (item: Message) => string[];
}

function MessageTable({ items, heading, columnHeaders, tableExtractor }: Props) {
  return (
    <>
      {heading && <h3>{heading}</h3>}
      {items.length > 0 && (
        <div className="tableContainer">
          <table className="table table-striped">
            <thead>
              <tr>
                {columnHeaders.map((header) => (
                  <th scope="col" key={header}>
                    {header}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {items.map((item, index) => {
                const rowData = tableExtractor(item);
                return (
                  <tr key={item["@type"] + index}>
                    {rowData.map((data, i) => (
                      <td key={i}>{data}</td>
                    ))}
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
}

export default MessageTable;
