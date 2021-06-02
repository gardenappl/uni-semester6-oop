import { NavLink } from "react-router-dom";

function Car(props) {
	return <NavLink to={`car/${props.carId}`}>
		<div class="car">
			<img src={props.thumbnailUrl}/>
			<span class="car-name">{props.manufacturer} {props.model}</span>
			<br />
			<span class="car-cost">{props.hrnPerDay}</span>
		</div>
	</NavLink>
}

function carToComponent(car) {
	return <Car
		key={car.id}
		carId={car.id}
		manufacturer={car.manufacturer} 
		model={car.model}
		hrnPerDay={car.hrnPerDay}
		thumbnailUrl={car.thumbnailUrl}
	/>
}

export { Car, carToComponent };
