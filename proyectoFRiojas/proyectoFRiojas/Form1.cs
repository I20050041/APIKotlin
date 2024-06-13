using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Net.Http.Json;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace proyectoFRiojas
{
    public partial class Form1 : Form
    {

        private readonly HttpClient _httpClient = new HttpClient { BaseAddress = new Uri("https://localhost:7107/api/") };

        public Form1()
        {
            InitializeComponent();
            LoadData();

        }
        private async void LoadData()
        {
            var categorias = await GetAllCategorias();
            dataGridView1.DataSource = categorias;
        }
        private async Task<List<Categoria>> GetAllCategorias()
        {
            var response = await _httpClient.GetFromJsonAsync<List<Categoria>>("Categoria");
            return response;
        }

        private async Task btnAgregar_ClickAsync(object sender, EventArgs e)
        {
            var nuevaCategoria = new Categoria
            {
                nombre = "Nuevo Nombre",
                descripcion = "Nueva Descripción",
                estatus = 1
            };
            await _httpClient.PostAsJsonAsync("Categoria", nuevaCategoria);
            LoadData();
        }

        private async void btnModificar_Click(object sender, EventArgs e)
        {
            if (dataGridView1.SelectedRows.Count > 0)
            {
                var categoria = (Categoria)dataGridView1.SelectedRows[0].DataBoundItem;
                categoria.nombre = txtnombre.Text;
                categoria.descripcion = txtdescricpion.Text;
                categoria.estatus = 1;

                try
                {
                    var response = await _httpClient.PutAsJsonAsync($"Categoria/{categoria.IdCategoria}", categoria);
                    if (response.IsSuccessStatusCode)
                    {
                        LoadData();
                    }
                    else
                    {
                        MessageBox.Show("Error al modificar la categoría.");
                    }
                }
                catch (Exception ex)
                {
                    MessageBox.Show($"Error al modificar la categoría: {ex.Message}");
                }
            }
            else
            {
                MessageBox.Show("Por favor, selecciona una categoría a modificar.");
            }
        }

        private async void btnEliminar_Click(object sender, EventArgs e)
        {
            if (dataGridView1.SelectedRows.Count > 0)
            {
                var categoria = (Categoria)dataGridView1.SelectedRows[0].DataBoundItem;
                try
                {
                    var response = await _httpClient.PutAsJsonAsync($"Categoria/inactivar/{categoria.IdCategoria}", categoria);
                    if (response.IsSuccessStatusCode)
                    {
                        LoadData();
                    }
                    else
                    {
                        MessageBox.Show("Error al inactivar la categoría.");
                    }
                }
                catch (Exception ex)
                {
                    MessageBox.Show($"Error al inactivar la categoría: {ex.Message}");
                }
            }
            else
            {
                MessageBox.Show("Por favor, selecciona una categoría a eliminar.");
            }
        }

        private void btnActualizar_Click(object sender, EventArgs e)
        {
            LoadData();

        }

        private async void btnBuscar_Click(object sender, EventArgs e)
        {
            int id = int.Parse(txtid.Text);
            BuscarCategoria(id);
        }

        private async void BuscarCategoria(int id)
        {
            var categoria = await _httpClient.GetFromJsonAsync<Categoria>($"Categoria/{id}");
            List<Categoria> result = new List<Categoria>();
            if (categoria != null)
            {
                result.Add(categoria);
            }
            dataGridView1.DataSource = result;
        }

        private async void btnAgregar_Click(object sender, EventArgs e)
        {
            var nuevaCategoria = new Categoria
            {
                nombre = txtnombre.Text,
                descripcion = txtdescricpion.Text,
                estatus = 1 // Suponiendo que siempre esté activo al agregar
            };
            await _httpClient.PostAsJsonAsync("Categoria", nuevaCategoria);
            LoadData();

        }
    }
}