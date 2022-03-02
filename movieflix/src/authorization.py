from flask import Blueprint, request, render_template, redirect, url_for, g, session

authorization = Blueprint("authorization", __name__)


@authorization.route("/login", methods=["GET", "POST"])
def login():
    # Redirect if already logged in
    if g.user:
        return redirect("/")

    if request.method == "GET":
        return render_template("login.html", fail=("fail" in request.args))
    else:
        if not ("email" in request.form and "password" in request.form):
            return redirect(url_for("authorization.login", fail=1))

        email = request.form["email"]
        password = request.form["password"]

        # Verify that the user exists
        match = g.users.find_one({
            "email": email
        })

        if match:
            # Verify credentials
            if password == match["password"]:
                session["email"] = email
                return redirect("/")
            else:
                return redirect(url_for("authorization.login", fail=1))
        else:
            return redirect(url_for("authorization.login", fail=1))


@authorization.route("/logout", methods=["GET"])
def logout():
    session.pop("email")
    return redirect("/")


@authorization.route("/register", methods=["GET", "POST"])
def register():
    # Redirect if already logged in
    if g.user:
        return redirect("/")

    if request.method == "GET":
        return render_template("register.html", fail=("fail" in request.args))
    else:
        if not ("name" in request.form and "email" in request.form and "password" in request.form):
            return redirect(url_for("authorization.login", fail=1))
        
        name = request.form["name"]
        email = request.form["email"]
        password = request.form["password"]

        # Verify that the user doesn't exist
        match = g.users.find_one({
            "email": email
        })

        if not match:
            g.users.insert_one({
                "name": name,
                "email": email,
                "password": password,
                "category": "user"
            })
            
            session["email"] = email
            
            return redirect("/")
        else:
            return redirect(url_for("authorization.register", fail=1))
