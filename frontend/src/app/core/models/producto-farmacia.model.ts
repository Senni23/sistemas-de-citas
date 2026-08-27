export interface ProductoFarmacia {
  id?: number;
  nombre: string;
  descripcion?: string;
  stock: number;
  stockMinimo: number;
  unidadMedida?: string;
  precioUnitario: number;
  fechaVencimiento?: string;
}
