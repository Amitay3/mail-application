
<p align="center"> <img src="ABAMail.webp" width="150"/> </p>

# Mail Web App

  

This project is a full-featured email management system with a **Node.js backend**, **React frontend**, and a **C++ Bloom filter server**, fully orchestrated using **Docker Compose**.

  

It includes sending, drafting, labeling, deleting, and spam filtering via a custom URL blacklist server.

  

---

  

## Running the Application with Docker Compose

  

The app consists of 4 services: `web-frontend`, `web-api`, `bloom-server`, and `tests`.

  

###  Prerequisites

* Docker & Docker Compose installed

* Port 5588 available (used by the Bloom server)

  

To free the port if it’s in use:

  

```bash

netstat  -aon | findstr  :5588

taskkill  /PID <pid> /F

```

  
  

---

  

### Start the App

  

In the project root, to build and run all containers, run:

  

```bash

docker  compose  up  --build

```

  

Once started, the frontend will be available at:

  

* [http://localhost:3000](http://localhost:3000)

  

The API will be running at:

  

* [http://localhost:8080](http://localhost:8080)

  

---

  

### Stopping the App

  

Use:

```bash

Ctrl  +  C

```

  

and then, to clean up used containers:

```bash

docker  compose  down  --remove-orphans

```

  

---
## Screenshots

  

### Running the app
In your terminal, in the project root, run command: docker  compose  up  --build  

![Run](docs/run.png)  

Successful build and run will result with this:  

![Result](docs/result.png)  

  

### User Registration And Sign-in
To register, fill the registration form and click submit.

![Register](docs/createUser.png)  

To sign in, fill the credentials and click sign-in.  

![Sign-in](docs/sign-in.png)  

  

### Compose New Mail

To create new mail, click compose button and fill the sender, recipient and content.  
Send button will send the mail, Cancel button will save it as draft.  

![Create Mail](docs/createMail.png)

  

### Mail Options - Label, Spam and Delete

Each mail has an options menu (☰), where you can assign labels (via checkboxes), report it as spam, or delete it.  
When you report a mail as spam, any URLs or links in it will be blacklisted.  

![Options](docs/options.png)


### Sign-out

To sign out, click your profile picture in the top-right corner and then click the sign-out button.  

![Sign-out](docs/sign-out.png)

---
  

### Folder General Structure

```

project-root/

├── backend/

│ ├── bloom-client/

│ ├── bloom-server/

│ └── web/                      ← Node.js API

├── frontend/Mail-app           ← React app

├── data/                       ← Bloom filter data

├── docs/

├── docker-compose.yml

├── .gitignore

└── README.md

```

  

---

  


## For Developers:

###  Frontend

  

The React frontend is in `frontend/mail-app/`.

  

It uses:

  

*  `React Router` for navigation

*  `Bootstrap` for styling

*  Self-made `Toast` notifications

* Dynamic dark/light mode support

  

To run frontend only:

  

```bash

cd  frontend/mail-app

npm  install

npm  start

```

  

---

  

### Backend

  

The Node.js Express backend is in `backend/web/`.

  

It uses:

  

* JWT-based authentication

* RESTful API for users, mails, labels, and blacklist

* `bloom-client` to filter spam URLs

  

To run backend only:

  

```bash

cd  backend/web

npm  install

node  app.js

```


  


  

---

 