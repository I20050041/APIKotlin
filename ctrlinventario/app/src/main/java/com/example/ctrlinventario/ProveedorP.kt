package com.example.ctrlinventario

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ProveedorP (
    @SerializedName("idProveedor")
    val idProveedor: Int,
    val nombre: String,
    val razonSocial: String,
    val direccion: String,
    val telefono: String,
    val rfc: String,
    val correo: String,
    val estatus: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idProveedor)
        parcel.writeString(nombre)
        parcel.writeString(razonSocial)
        parcel.writeString(direccion)
        parcel.writeString(telefono)
        parcel.writeString(rfc)
        parcel.writeString(correo)
        parcel.writeInt(estatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProveedorP> {
        override fun createFromParcel(parcel: Parcel): ProveedorP {
            return ProveedorP(parcel)
        }

        override fun newArray(size: Int): Array<ProveedorP?> {
            return arrayOfNulls(size)
        }
    }
}
