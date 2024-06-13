
import com.example.ctrlinventario.ArticuloP
import com.example.ctrlinventario.CategoriaP
import com.example.ctrlinventario.ProveedorP
import com.example.ctrlinventario.Usuario
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PApiService {
    @GET("api/Usuario/autenticar")
    fun autenticarUsuario(
        @Query("correo") correo: String,
        @Query("contrasena") contrasena: String
    ): Call<ResponseBody>
    // CRUD Artículos
    @POST("api/Articulos")
    fun addArticulo(@Body articulo: ArticuloP): Call<List<ArticuloP>>

    @GET("api/Articulos")
    fun getAllArticulos(): Call<List<ArticuloP>>

    @PUT("api/Articulos/{id}")
    fun updateArticulo(@Path("id") id: Int, @Body articulo: ArticuloP): Call<ArticuloP>

    @PUT("api/Articulos/inactivar/{id}")
    fun inactivarArticulo(@Path("id") id: Int): Call<Void>
    // CRUD Categoria
    @POST("api/Categoria")
    fun addCategoria(@Body categoria: CategoriaP): Call<List<CategoriaP>>

    @GET("api/Categoria")
    fun getAllCategorias(): Call<List<CategoriaP>>

    @PUT("api/Categoria/{id}")
    fun updateCategoria(@Path("id") id: Int, @Body categoria: CategoriaP): Call<CategoriaP>

    @PUT("api/Categoria/inactivar/{id}")
    fun inactivarCategoria(@Path("id") id: Int): Call<Void>


    // CRUD Proveedor
    @POST("api/Proveedor")
    fun addProveedor(@Body proveedor: ProveedorP): Call<List<ProveedorP>>

    @GET("api/Proveedor")
    fun getAllProveedor(): Call<List<ProveedorP>>

    @PUT("api/Proveedor/{id}")
    fun updateProveedor(@Path("id") id: Int, @Body proveedor: ProveedorP): Call<ProveedorP>

    @PUT("api/Proveedor/inactivar/{id}")
    fun inactivarProveedor(@Path("id") id: Int): Call<Void>

    // CRUD Usuario
    @POST("api/Usuario")
    fun addUsuario(@Body usuario: Usuario): Call<List<Usuario>>

    @GET("api/Usuario")
    fun getAllUsuarios(): Call<List<Usuario>>
}