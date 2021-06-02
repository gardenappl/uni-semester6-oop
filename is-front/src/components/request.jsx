import React, { Component } from "react";
import { fetchPostJson } from "../utils.js";
import API_SERVER from "../Constants.js";


class RequestInfo extends Component {
	constructor(props) {
		super(props);

		this.onAccept = this.onAccept.bind(this);
	}
	onAccept() {
		console.log(this.props);
		fetchPostJson(API_SERVER + "/admin-request", {
			token: localStorage.getItem('token'),
			requestId: this.props.requestId,
			action: 'approve'
		}).then((result) => {
			if (result['success'])
				this.props.onStatusChange(this.props.requestId);
		});
	}

	render() {
		const priceFormat = new Intl.NumberFormat('uk-UA', { style: 'currency', currency: 'UAH' });
		return <div class="rent-request">
			<span class="user-id">Passport ID: {this.props.passportId}</span>
			<br />
			<span class="car">{this.props.manufacturer} {this.props.model}</span>
			<br />
			<span class="time">{this.props.startDate}, {this.props.days} days</span>
			<br />
			<span class="total-cost">{priceFormat.format(this.props.cost)}</span>
			<button type="button" onClick={this.onAccept}>Approve</button>
		</div>
	}

}

function requestInfoToComponent(requestInfo, onStatusChange) {
	console.log(requestInfo);
	return <RequestInfo
		key={requestInfo.requestId}
		requestId={requestInfo.requestId}
		passportId={requestInfo.passportId}
		manufacturer={requestInfo.carManufacturer}
		model={requestInfo.carModel}
		startDate={requestInfo.startDate}
		days={requestInfo.days}
		cost={requestInfo.hrnPerDay * requestInfo.days}
		onStatusChange={onStatusChange}
	/>
}

export { RequestInfo, requestInfoToComponent };
