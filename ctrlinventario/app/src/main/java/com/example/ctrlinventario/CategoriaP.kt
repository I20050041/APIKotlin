package com.example.ctrlinventario
import android.os.Parcel
import android.os.Parcelable

data class CategoriaP (
    val idCategoria: Int,
    val nombre: String,
    val descripcion: String,
    val estatus: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idCategoria)
        parcel.writeString(nombre)
        parcel.writeString(descripcion)
        parcel.writeInt(estatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoriaP> {
        override fun createFromParcel(parcel: Parcel): CategoriaP {
            return CategoriaP(parcel)
        }

        override fun newArray(size: Int): Array<CategoriaP?> {
            return arrayOfNulls(size)
        }
    }
}