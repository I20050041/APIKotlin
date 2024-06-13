package com.example.ctrlinventario

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ArticuloP (
    @SerializedName("idArticulos")
    val idArticulo: Int,
    val nombre: String,
    val descripcion: String,
    val precioUnitario: Double,
    val stock: Int,
    val estatus: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idArticulo)
        parcel.writeString(nombre)
        parcel.writeString(descripcion)
        parcel.writeDouble(precioUnitario)
        parcel.writeInt(stock)
        parcel.writeInt(estatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ArticuloP> {
        override fun createFromParcel(parcel: Parcel): ArticuloP {
            return ArticuloP(parcel)
        }

        override fun newArray(size: Int): Array<ArticuloP?> {
            return arrayOfNulls(size)
        }
    }
}
