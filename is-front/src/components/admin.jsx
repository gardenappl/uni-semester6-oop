import React, { Component } from "react";
import { fetchPostJson } from "../utils.js";
import API_SERVER, { STATUS_PENDING } from "../Constants.js";
import { requestInfoToComponent } from "./request.jsx";

class RequestsAdmin extends Component {
	constructor(props) {
		super(props);
		this.state = {
			requestInfos: []
		}
		this.getRequests = this.getRequests.bind(this);
		this.getRequests();
	}
	getRequests() {
		fetchPostJson(API_SERVER + "/list-requests", {
			token: localStorage.getItem('token'),
			status: STATUS_PENDING
		})
		.then((result) => {
			console.log(result);
			this.setState({
				requestInfos: result['requests']
			});
		});
	}
	render() {
		return <div>
			<h2>Pending requests:</h2>
			<div class="requests">
				{this.state.requestInfos.map((requestInfo) => {
					return requestInfoToComponent(requestInfo, this.getRequests);
				})}
			</div>
		</div>
	}
}

export default RequestsAdmin;
