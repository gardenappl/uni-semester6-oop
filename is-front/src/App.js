import './App.css';
import {
	Route,
	NavLink,
	HashRouter
} from "react-router-dom";
import Login from "./components/login.jsx";
import Register from "./components/register.jsx";
import Cars from "./components/cars.jsx";
import CarInfo from "./components/car-info.jsx";
import Request from "./components/new-request.jsx";
import RequestsAdmin from "./components/admin.jsx";
import MyStatus from "./components/main.jsx";

function App() {
  return (
	  <HashRouter>

 <div class="App">
          <h1>Car Rental</h1>
          <ul className="header">
	    <li><NavLink exact to="/">Main</NavLink></li>
	    <li><NavLink to="/cars">Cars</NavLink></li>
            <li><NavLink to="/login">Log In</NavLink></li>
	    {localStorage.getItem('isAdmin') === 'true' && <li><NavLink to="/admin">Admin</NavLink></li>}
          </ul>
          <div className="content">
            <Route path="/login" component={Login}/>
            <Route path="/register" component={Register}/>
            <Route path="/car" component={CarInfo}/>
            <Route path="/request" component={Request}/>
	    <Route path="/admin" component={RequestsAdmin}/>
            <Route path="/cars" component={Cars}/>
            <Route exact path="/" component={MyStatus}/>
          </div>
        </div>

	  </HashRouter>
  );
}

export default App;
