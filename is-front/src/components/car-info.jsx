import React, { Component } from "react";
import { fetchPostJson, lastSegment } from "../utils.js";
import { NavLink } from "react-router-dom";
import API_SERVER from "../Constants.js";

class CarInfo extends Component {
	constructor(props) {
		super(props);
		this.state = {
			model: "",
			manufacturer: "",
			thumbnailUrl: "",
			hrnPerDay: ""
		}
		fetchPostJson(API_SERVER + "/cars", {
			carId: lastSegment(window.location.href)
		})
		.then((result) => {
			console.log(result);
			this.setState(result.cars[0]);
		});
	}
	render() {
		const priceFormat = new Intl.NumberFormat('uk-UA', { style: 'currency', currency: 'UAH' });
		return <div>
			<h2 id="manufacturer">{this.state.manufacturer}</h2>
			<h3 id="model">{this.state.model}</h3>
			<img src={this.state.thumbnailUrl} />
			<span class="price">Price: <b>{priceFormat.format(this.state.hrnPerDay)}</b> per day</span>
			<br />
			<NavLink to={`request/${lastSegment(window.location.href)}`}>Rent now</NavLink>
		</div>
	}
}

export default CarInfo;
