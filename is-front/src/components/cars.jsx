import React, { Component } from "react";
import { fetchPostJson } from "../utils.js";
import API_SERVER from "../Constants.js";
import { carToComponent } from "./car.jsx";

class Cars extends Component {
	constructor(props) {
		super(props);
		this.state = {
			cars: []
		}
		console.log("Cars:");
		fetchPostJson(API_SERVER + "/cars", {
			getAllAvailableCars: true
		})
		.then((result) => {
			console.log(result);
			this.setState({
				cars: result['cars']
			});
		});
	}
	render() {
		return <div>
			<h2>Available cars:</h2>
			<div class="cars">
				{this.state.cars.map(carToComponent)}
			</div>
		</div>
	}
}

export default Cars;
