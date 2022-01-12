package texnopos.uz.db

import org.ktorm.database.Database

object DatabaseConnection {
    val database = Database.connect(
        url = "jdbc:mysql://f2fbe0zvg9j8p9ng.cbetxkdyhwsb.us-east-1.rds.amazonaws.com:3306/fdc94tf9xe07b91l",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "cs2elkqtt17mlywz",
        password = "kqzn6n3pisafyu67"
    )
}