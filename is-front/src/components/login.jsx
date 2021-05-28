import React, { Component } from "react";
import API_SERVER from "../Constants.js";

class Login extends Component {
	constructor(props) {
		super(props);
		this.state = {loaded: false, id: 0};
	}

	componentDidMount() {
		fetch(API_SERVER + "/register")
		.then((response) => {
			return response.json();
		})
		.then((json) => {
			this.setState({
				loaded: true,
				id: json.currentCar
			});
		});
	}

	render() {
		return (<div hidden={!this.state.loaded}>{this.state.id}</div>);
	}
}

export default Login;
