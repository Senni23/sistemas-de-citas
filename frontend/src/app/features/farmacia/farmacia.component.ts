import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoFarmacia } from '../../core/models/producto-farmacia.model';
import { ProductoFarmaciaService } from '../../core/services/producto-farmacia.service';

@Component({
  selector: 'app-farmacia',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './farmacia.component.html',
})
export class FarmaciaComponent implements OnInit {
  productos: ProductoFarmacia[] = [];
  formulario: ProductoFarmacia = this.formularioVacio();
  editando = false;

  constructor(private productoFarmaciaService: ProductoFarmaciaService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.productoFarmaciaService.listar().subscribe((data) => (this.productos = data));
  }

  guardar(): void {
    if (this.editando && this.formulario.id) {
      this.productoFarmaciaService.actualizar(this.formulario.id, this.formulario).subscribe(() => {
        this.cargar();
        this.cancelar();
      });
    } else {
      this.productoFarmaciaService.crear(this.formulario).subscribe(() => {
        this.cargar();
        this.cancelar();
      });
    }
  }

  editar(producto: ProductoFarmacia): void {
    this.formulario = { ...producto };
    this.editando = true;
  }

  eliminar(id: number): void {
    if (confirm('Desea eliminar este producto?')) {
      this.productoFarmaciaService.eliminar(id).subscribe(() => this.cargar());
    }
  }

  cancelar(): void {
    this.formulario = this.formularioVacio();
    this.editando = false;
  }

  stockBajo(producto: ProductoFarmacia): boolean {
    return producto.stock <= producto.stockMinimo;
  }

  private formularioVacio(): ProductoFarmacia {
    return { nombre: '', descripcion: '', stock: 0, stockMinimo: 0, unidadMedida: '', precioUnitario: 0 };
  }
}
