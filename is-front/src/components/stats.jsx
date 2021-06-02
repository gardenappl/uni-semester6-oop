import React, { Component } from "react";
import { fetchPostJson } from "../utils.js";
import { NavLink } from "react-router-dom";
import API_SERVER  from "../Constants.js";

function CarStatistic(props) {
	const priceFormat = new Intl.NumberFormat('uk-UA', { style: 'currency', currency: 'UAH' });
	return <tr>
		<td>
			<NavLink to={`car/${props.carId}`}>
				{props.manufacturer} {props.model}
			</NavLink>
		</td>
		<td>
			{priceFormat.format(props.hrnProfit)}
		</td>
	</tr>;
}

function carStatisticToComponent(carStatistic) {
	return <CarStatistic
		key={carStatistic.id}
		carId={carStatistic.id}
		manufacturer={carStatistic.manufacturer} 
		model={carStatistic.model}
		hrnProfit={carStatistic.hrnProfit}
	/>
}

class AdminStats extends Component {
	constructor(props) {
		super(props);
		this.state = {
			topProfitableCars: []
		}
		this.getRequests = this.getRequests.bind(this);
		this.getRequests();
	}
	getRequests() {
		fetchPostJson(API_SERVER + "/top-cars", {
			token: localStorage.getItem('token')
		})
		.then((result) => {
			console.log(result);
			this.setState({
				topProfitableCars: result['carStatistics']
			});
		});
	}
	render() {
		return <div>
			<h2>Most profitable cars of all time:</h2>
			<table class="car-stats">
				{this.state.topProfitableCars.map((carStatistic) => {
					return carStatisticToComponent(carStatistic);
				})}
			</table>
		</div>
	}
}

export default AdminStats;
