# Model representing documents from the User collection
class User:
    def __init__(self, name: str, email: str, password: str, category: str = "user"):
        self.name = name
        self.email = email
        self.password = password
        self.category = category

    def __repr__(self):
        print(f"<User: {self.email} ({self.category})>")

    @staticmethod
    def make_from_document(user: dict):
        """
        Creates a User object from a User MongoDB document

        Parameters
        ----------
        user : dict
            The User MongoDB document

        Returns
        -------
        models.User
            The resulting User object
        """
        return User(
            user["name"],
            user["email"],
            user["password"],
            user["category"]
        )
