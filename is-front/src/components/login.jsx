import React, { Component } from "react";
import { NavLink, withRouter } from "react-router-dom";
import { fetchPostJson } from "../utils.js";
import API_SERVER from "../Constants.js";

class Login extends Component {
	constructor(props) {
		super(props);
		this.state = {
			username: "",
			password: ""
		};

		this.handleChange = this.handleChange.bind(this);
		this.handleSubmit = this.handleSubmit.bind(this);
	}
	handleChange(event) {
		this.setState({
			[event.target.name]: event.target.value
		});
	}
	handleSubmit(event) {
		console.log("Logging in");
		fetchPostJson(API_SERVER + "/login", this.state)
		.then((json) => {
			if (typeof json['token'] === 'number') {
				localStorage.setItem("token", json['token']);
				console.log(`Got token ${json['token']}`);
				withRouter(({ history }) => {
					history.push('/');
				});
			} else {
				alert("Wrong username or password");
			}
		});
		event.preventDefault();
	}
	render() {
		return <div>
			<form onSubmit={this.handleSubmit}>
				<label>
					Username:
					<input type="text" name="username" onChange={this.handleChange} />
				</label>
				<br/>
				<label>
					Password:
					<input type="password" name="password" onChange={this.handleChange} />
				</label>
				<br/>
				<input type="submit" value="Log in" />
			</form>
			<NavLink to="/register">Register</NavLink>
		</div>
	}
}

export default Login;
