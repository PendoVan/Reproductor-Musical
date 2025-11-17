import os

def crear_directorio_si_no_existe(ruta: str) -> None:
    """Crea la carpeta si no existe."""
    if not os.path.exists(ruta):
        os.makedirs(ruta, exist_ok=True)
        print(f"Directorio creado: {ruta}")
    else:
        print(f"El directorio ya existe: {ruta}")
