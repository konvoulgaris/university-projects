# movieflix
A movie database management web application, originally developed for the requirements of the course [Information Systems (DS-512)](https://www.ds.unipi.gr/en/courses/information-systems-2/).

It is designed to be an IMDb-like web application that allows users to see information about movies, leave ratings and / or comments and see other people's ratings and / or comments. Additionally, there are special users (i.e. admins) which have the ability to edit all movie and user data using an admin-only "Manage" page.

## Default Accounts
| Name | E-Mail | Password | Category |
| --- | --- | --- | --- |
| Admin | admin@movieflix.com | admin | admin |
| John | john@mail.com | john | user |
| mary | mary@mail.com | mary | user |
| bob | bob@mail.com | bob | user |

## Default Movies
| Name | Year | Ratings | Comments |
| --- | --- | --- | --- |
| Star Wars | 1977 | 4 | 4 |
| Star Wars: The Empire Strikes Back | 1980 | 3 | 3 |
| Star Wars: Return of the Jedi | 1983 | 2 | 1 |
| Walk the Line | 2005 | 2 | 1 |
| Bohemian Rhapsody | 2018 | 4 | 3 |
| This Is It | 2009 | 3 | 2 |

## How to Run
To make running this application easier, a docker-compose file is provided which automatically sets up a MongoDB database, the web application and their network. Simply open your terminal, run the following command:

```bash
docker-compose up
```

And then go to the following address:

[http://localhost:5000](http://localhost:5000)

Enjoy! 😊

## License
This project is licensed under the [Apache Licence Version 2.0](LICENSE) by [Konstantinos Voulgaris](mailto:konstantinos@voulgaris.info)
