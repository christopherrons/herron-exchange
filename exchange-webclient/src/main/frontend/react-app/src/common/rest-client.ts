const URL: string = "http://localhost:8087/exchangeData"

export const fetchOrderbookState = async (orderbookId: string): Promise<string> => {
    const response = await fetch(`${URL}/orderbookState?orderbookId=${orderbookId}`);
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    return response.json();
  };