db.createUser(
    {
        user: "gal-root",
        pwd: "toortoor71",
        roles: [
            {
                role: "dbOwner",
                db: "img-gallery"
            }
        ]
    }
)
