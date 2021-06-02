import React, { Component } from "react";
import { fetchPostJson } from "../utils.js";
import API_SERVER, { STATUS_REPAIR_NEEDED, STATUS_PENDING, STATUS_ACTIVE } from "../Constants.js";


class RequestInfo extends Component {
	constructor(props) {
		super(props);

		this.onAccept = this.onAccept.bind(this);
		this.onDeny = this.onDeny.bind(this);
		this.onEnd = this.onEnd.bind(this);
		this.onRepairNeeded = this.onRepairNeeded.bind(this);
		this.payRepair = this.payRepair.bind(this);
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

	onDeny() {
		console.log(this.props);
		const reason = prompt("Enter reason for denial");
		fetchPostJson(API_SERVER + "/admin-request", {
			token: localStorage.getItem('token'),
			requestId: this.props.requestId,
			action: 'deny',
			actionMessage: reason
		}).then((result) => {
			if (result['success'])
				this.props.onStatusChange(this.props.requestId);
		});
	}

	onEnd() {
		console.log(this.props);
		const sum = prompt("Enter maintenance cost (e.g. '250' for 250 UAH)");
		fetchPostJson(API_SERVER + "/admin-request", {
			token: localStorage.getItem('token'),
			requestId: this.props.requestId,
			action: 'end',
			repairCostHrn: sum
		}).then((result) => {
			if (result['success'])
				this.props.onStatusChange(this.props.requestId);
		});
	}

	onRepairNeeded() {
		console.log(this.props);
		const reason = prompt("Enter reason for repair costs");
		const sum = prompt("Enter repair cost (e.g. '1000' for 1000 UAH)");
		fetchPostJson(API_SERVER + "/admin-request", {
			token: localStorage.getItem('token'),
			requestId: this.props.requestId,
			action: 'needs-repair',
			actionMessage: reason,
			repairCostHrn: sum
		}).then((result) => {
			if (result['success'])
				this.props.onStatusChange(this.props.requestId);
		});
	}

	payRepair() {
		fetchPostJson(API_SERVER + "/repair", {
			hrnAmount: this.props.repairCost,
			requestId: this.props.requestId
		}).then((result) => {
			if (result['success']) {
				console.log("Thank you, please don't do this again.");
				this.props.onStatusChange(this.props.requestId);
			}
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
			{this.props.status === STATUS_PENDING && <button type="button" onClick={this.onAccept}>Approve</button>}
			{this.props.status === STATUS_PENDING && <button type="button" onClick={this.onDeny}>Deny</button>}
			{this.props.status === STATUS_ACTIVE && <button type="button" onClick={this.onEnd}>Car returned</button>}
			{this.props.status === STATUS_ACTIVE && <button type="button" onClick={this.onRepairNeeded}>Car returned (damaged)</button>}
			{this.props.message && <div class="message">{this.props.message}</div>}
			{this.props.status === STATUS_REPAIR_NEEDED && <button type="button" onClick={this.payRepair}>Pay {priceFormat.format(this.props.repairCost)}</button>}
		</div>
	}

}

function requestInfoToComponent(requestInfo, onStatusChange) {
	console.log(requestInfo);
	return <RequestInfo
		key={requestInfo.requestId}
		status={requestInfo.status}
		message={requestInfo.message}
		requestId={requestInfo.requestId}
		passportId={requestInfo.passportId}
		manufacturer={requestInfo.carManufacturer}
		model={requestInfo.carModel}
		startDate={requestInfo.startDate}
		days={requestInfo.days}
		cost={requestInfo.hrnPerDay * requestInfo.days}
		onStatusChange={onStatusChange}
		repairCost={requestInfo.repairCost}
	/>
}

export { RequestInfo, requestInfoToComponent };