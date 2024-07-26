import { useState } from "react";
import { Message } from "../common/Types";

interface Props {
  items: Message[];
  heading: string;
  columnHeaders: string[];
  tableExtractor: (item: Message) => string[];
}

function MessageTable({ items, heading, columnHeaders, tableExtractor }: Props) {
  return (
    <>
      <h1>{heading}</h1>
      {items.length > 0 && (
        <table className="table">
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
                    <td scope="row" key={i}>
                      {data}
                    </td>
                  ))}
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </>
  );
}

export default MessageTable;
