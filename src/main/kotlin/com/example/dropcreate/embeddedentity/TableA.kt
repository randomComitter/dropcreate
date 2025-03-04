package com.example.dropcreate.embeddedentity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class TableA(
    @Id
    val id: Int,

    @Column
    val payload: String?
)
