package com.example.exceptions


/**
 * Created on 30.12.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
class NotFoundException(
    message: String
) : RuntimeException(message)