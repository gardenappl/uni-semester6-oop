import './App.css';
import {
	Route,
	NavLink,
	HashRouter
} from "react-router-dom";
import Login from "./components/login.jsx";

function App() {
  return (
	  <HashRouter>

 <div>
          <h1>Car Rental</h1>
          <ul className="header">
            <li><NavLink to="/">Log In</NavLink></li>
          </ul>
          <div className="content">
            <Route exact path="/" component={Login}/>
          </div>
        </div>

	  </HashRouter>
  );
}

export default App;
