org_vaadin_leif_selectrenderer_SelectRenderer = function() {
	var self = this;

	self.init = function(cell) {
		var select = document.createElement('select');

		var options = self.getState().options;
		for (var i = 0; i < options.length; i++) {
			var option = document.createElement('option');
			option.appendChild(document.createTextNode(options[i]));
			option.value = i;

			select.appendChild(option);
		}

		cell.element.appendChild(select);
	};

	self.render = function(cell, data) {
		var select = cell.element.childNodes[0];
		select.value = "";
		if (data !== null) {
			select.childNodes[data].selected = true;
		}
	};

	self.getConsumedEvents = function() {
		// Must capture click to prevent Grid from changing selection and
		// closing the popup
		return [ 'click', 'change' ];
	};

	self.onBrowserEvent = function(cell, event) {
		if (event.type === 'change') {
			self.change(self.getRowKey(cell.rowIndex),
					+cell.element.childNodes[0].value);
		}
		return true;
	};
}