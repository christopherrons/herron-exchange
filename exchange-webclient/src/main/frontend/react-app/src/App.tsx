import "./App.css";
import Footer from "./components/common/Footer";
import MarketSummary from "./components/market-summary/MarketSummary";

function App() {
  return (
    <div className="App">
      <div className="content">
        <div>
          <MarketSummary></MarketSummary>
        </div>
      </div>
      <div className="footer">
        <Footer />
      </div>
    </div>
  );
}

export default App;
