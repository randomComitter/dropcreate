package com.example.dropcreate.externalentity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class TableB(
    @Id
    val id: Int,

    @Column
    val payload: String?
)
