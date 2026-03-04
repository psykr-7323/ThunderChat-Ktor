package services

import org.mindrot.jbcrypt.BCrypt
import repositories.UserRepository
import security.JwtConfig

class UserService (private val userRepository: UserRepository){
    fun authenticate(username: String, password: String) : String? {
        val userInDb = userRepository.findByUsername(username) ?: return null

        val passwordMatches = BCrypt.checkpw(password, userInDb.password)

        return if (passwordMatches) {
            JwtConfig.generateToken(userInDb.username)
        } else {
            null
        }
    }

    fun register(username: String, password: String, email: String) : Boolean {
        val exist = userRepository.findByUsername(username)
        if (exist != null) {
            return false
        }
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
        return userRepository.createUser(username, passwordHash, email)
    }
}