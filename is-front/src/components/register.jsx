import React, { Component } from "react";
import { withRouter } from "react-router-dom";
import { fetchPostJson } from "../utils.js";
import API_SERVER from "../Constants.js";

class Register extends Component {
	constructor(props) {
		super(props);
		this.state = {
			username: "",
			password: "",
			passportId: ""
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
		fetchPostJson(API_SERVER + "/register", this.state)
		.then((json) => {
			if (typeof json['token'] === 'number') {
				localStorage.setItem("token", json['token']);
				console.log(`Got token ${json['token']}`);
				withRouter(({ history }) => {
					history.push('/');
				});
			} else {
				alert("This username or passport ID is already taken.");
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
				<label>
					Passport ID:
					<input type="text" name="passportId" onChange={this.handleChange} />
				</label>
				<br/>
				<input type="submit" value="Register" />
			</form>
		</div>
	}
}

export default Register;
