import os

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import FileResponse

from app.config import DOWNLOAD_DIR
from app.downloader import buscar_video, descargar_mp3
from app.models import CancionesRequest

app = FastAPI(title="YT Music Downloader API")

# Habilitar CORS para poder llamar desde tu app JavaFX (localhost)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/ping")
def ping():
    return {"status": "ok"}


@app.get("/buscar")
def buscar(query: str):
    """Devuelve la URL del primer resultado de YouTube para la consulta dada."""
    url = buscar_video(query)
    if not url:
        raise HTTPException(status_code=404, detail="No se encontró ningún resultado")
    return {"query": query, "url": url}


@app.post("/descargar")
def descargar_canciones(request: CancionesRequest):
    """Busca y descarga en MP3 cada término de la lista 'canciones'."""
    resultados = []

    for cancion in request.canciones:
        try:
            url = buscar_video(cancion)
            if not url:
                raise Exception("No se encontró video")

            nombre_archivo = descargar_mp3(url)

            resultados.append(
                {
                    "nombre": cancion,
                    "url": url,
                    "archivo": nombre_archivo,
                    "estado": "descargado",
                }
            )
        except Exception as e:  # noqa: BLE001
            resultados.append(
                {
                    "nombre": cancion,
                    "url": None,
                    "archivo": None,
                    "estado": f"error: {e}",
                }
            )

    return {"resultados": resultados}


@app.get("/descargas")
def listar_descargas():
    """Lista todos los MP3 que hay en la carpeta de descargas."""
    if not os.path.exists(DOWNLOAD_DIR):
        return {"archivos": []}
    archivos = [f for f in os.listdir(DOWNLOAD_DIR) if f.endswith(".mp3")]
    return {"archivos": archivos}


@app.get("/descargas/{nombre_archivo}")
def obtener_mp3(nombre_archivo: str):
    """Devuelve un archivo MP3 concreto."""
    ruta = os.path.join(DOWNLOAD_DIR, nombre_archivo)
    if os.path.exists(ruta):
        return FileResponse(path=ruta, filename=nombre_archivo, media_type="audio/mpeg")
    raise HTTPException(status_code=404, detail="Archivo no encontrado")


@app.delete("/descargas/{nombre_archivo}")
def borrar_archivo(nombre_archivo: str):
    ruta = os.path.join(DOWNLOAD_DIR, nombre_archivo)
    if os.path.exists(ruta):
        os.remove(ruta)
        return {"mensaje": f"'{nombre_archivo}' fue eliminado correctamente."}
    raise HTTPException(status_code=404, detail="Archivo no encontrado")


@app.delete("/descargas")
def borrar_todos_los_archivos():
    if not os.path.exists(DOWNLOAD_DIR):
        raise HTTPException(status_code=404, detail="Directorio de descargas no encontrado")

    archivos_eliminados = []
    for archivo in os.listdir(DOWNLOAD_DIR):
        if archivo.endswith(".mp3"):
            os.remove(os.path.join(DOWNLOAD_DIR, archivo))
            archivos_eliminados.append(archivo)

    return {"mensaje": "Se eliminaron los archivos", "archivos": archivos_eliminados}
