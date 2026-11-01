// Which booking are we editing? null means we are adding a new one.
let editingId = null;

const messageBox = document.getElementById('message');

function showMessage(text, isError) {
  messageBox.textContent = text;
  messageBox.className = isError ? 'error' : 'ok';
  messageBox.style.display = 'block';
}

function clearMessage() {
  messageBox.style.display = 'none';
}

// Adds one table cell with some text
function addCell(row, text) {
  const cell = document.createElement('td');
  cell.textContent = text;
  row.appendChild(cell);
}

// GET /venues: fill the venue table and the drop-down list
async function loadVenues() {
  const response = await fetch('/venues');
  const venues = await response.json();

  const rows = document.getElementById('venueRows');
  const select = document.getElementById('venueName');
  rows.innerHTML = '';
  select.innerHTML = '';

  for (const v of venues) {
    const row = document.createElement('tr');
    addCell(row, v.name);
    addCell(row, v.capacity);
    addCell(row, v.location);
    addCell(row, v.maxBookingHours);
    rows.appendChild(row);

    const option = document.createElement('option');
    option.value = v.name;
    option.textContent = v.name;
    select.appendChild(option);
  }
}

// GET /bookings: fill the bookings table
async function loadBookings() {
  const response = await fetch('/bookings');
  const bookings = await response.json();

  const rows = document.getElementById('bookingRows');
  rows.innerHTML = '';

  for (const b of bookings) {
    const row = document.createElement('tr');
    addCell(row, b.id);
    addCell(row, b.venueName);
    addCell(row, b.bookerName);
    addCell(row, b.date);
    addCell(row, b.startHour + ':00');
    addCell(row, b.endHour + ':00');

    const actions = document.createElement('td');

    const editButton = document.createElement('button');
    editButton.textContent = 'Edit';
    editButton.onclick = () => startEditing(b);
    actions.appendChild(editButton);

    const deleteButton = document.createElement('button');
    deleteButton.textContent = 'Cancel booking';
    deleteButton.onclick = () => cancelBooking(b.id);
    actions.appendChild(deleteButton);

    row.appendChild(actions);
    rows.appendChild(row);
  }
}

// Put a booking into the form so the user can change it
function startEditing(b) {
  editingId = b.id;
  document.getElementById('venueName').value = b.venueName;
  document.getElementById('bookerName').value = b.bookerName;
  document.getElementById('date').value = b.date;
  document.getElementById('startHour').value = b.startHour;
  document.getElementById('endHour').value = b.endHour;
  document.getElementById('formTitle').textContent = 'Editing booking ' + b.id;
  document.getElementById('saveButton').textContent = 'Save changes';
  document.getElementById('cancelEditButton').style.display = 'inline';
}

function stopEditing() {
  editingId = null;
  document.getElementById('bookingForm').reset();
  document.getElementById('formTitle').textContent = 'New booking';
  document.getElementById('saveButton').textContent = 'Book venue';
  document.getElementById('cancelEditButton').style.display = 'none';
}

// DELETE /bookings/{id}
async function cancelBooking(id) {
  if (!confirm('Cancel booking ' + id + '?')) {
    return;
  }
  clearMessage();
  const response = await fetch('/bookings/' + id, { method: 'DELETE' });
  if (response.ok) {
    showMessage('Booking cancelled.', false);
    await loadBookings();
  } else {
    const text = await response.text();
    showMessage(text || ('Error ' + response.status), true);
  }
}

// POST /bookings (new) or PUT /bookings/{id} (editing)
document.getElementById('bookingForm').addEventListener('submit', async (event) => {
  event.preventDefault(); // stop the page from reloading
  clearMessage();

  const booking = {
    venueName: document.getElementById('venueName').value,
    bookerName: document.getElementById('bookerName').value,
    date: document.getElementById('date').value,
    startHour: Number(document.getElementById('startHour').value),
    endHour: Number(document.getElementById('endHour').value)
  };

  const wasEditing = editingId !== null;
  const url = wasEditing ? '/bookings/' + editingId : '/bookings';
  const method = wasEditing ? 'PUT' : 'POST';

  const response = await fetch(url, {
    method: method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(booking)
  });

  if (response.ok) {
    showMessage(wasEditing ? 'Booking updated.' : 'Booking saved.', false);
    stopEditing();
    await loadBookings();
  } else {
    // 409 = clash, 400 = bad input, 404 = not found. The server sends the message.
    const text = await response.text();
    showMessage(text || ('Error ' + response.status), true);
  }
});

document.getElementById('cancelEditButton').addEventListener('click', stopEditing);

// run when the page opens
loadVenues();
loadBookings();