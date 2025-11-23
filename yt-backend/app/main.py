import os

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import FileResponse
from typing import List

from app.config import DOWNLOAD_DIR
from app.downloader import buscar_video, descargar_mp3
from app.models import CancionesRequest

app = FastAPI(title="YT Music Downloader API")


app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/ping")
def ping():
    """Health check endpoint para verificar que el servidor está vivo."""
    return {
        "status": "ok",
        "message": "Backend FastAPI funcionando correctamente",
        "version": "1.0.0"
    }



@app.get("/buscar")
def buscar_canciones(q: str, max_results: int = 10):
    """
    Busca videos en YouTube SIN descargarlos.
    Devuelve lista de resultados para que el usuario elija.
    """
    if not q or q.strip() == "":
        raise HTTPException(status_code=400, detail="Parámetro 'q' requerido")
    
    import yt_dlp
    
  
    ydl_opts = {
        'quiet': True,
        'no_warnings': True,
        'extract_flat': 'in_playlist',  # CAMBIADO
        'skip_download': True,           # AGREGADO
        'format': 'best',
    }
    
    try:
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:

            search_query = f"ytsearch{max_results}:{q}"
            info = ydl.extract_info(search_query, download=False)
            
            resultados = []
            

            if 'entries' in info:
                entries = info['entries']
            else:
                entries = [info]
            
            for entry in entries:
                if entry:

                    titulo = entry.get('title', 'Desconocido')
                    video_id = entry.get('id', '')
                    duracion = entry.get('duration', 0)
                    

                    artista = None
                    if ' - ' in titulo:
                        partes = titulo.split(' - ', 1)
                        artista = partes[0].strip()
                    

                    thumbnail = ''
                    if 'thumbnail' in entry:
                        thumbnail = entry['thumbnail']
                    elif 'thumbnails' in entry and len(entry['thumbnails']) > 0:
                        thumbnail = entry['thumbnails'][-1].get('url', '')
                    
                    resultado = {
                        "video_id": video_id,
                        "titulo": titulo,
                        "artista": artista,
                        "duracion": duracion if duracion else 0,
                        "thumbnail": thumbnail,
                        "url": f"https://youtube.com/watch?v={video_id}"
                    }
                    resultados.append(resultado)
            
            print(f"[Backend] Búsqueda '{q}': {len(resultados)} resultados encontrados")
            return {"resultados": resultados}
    
    except Exception as e:
        print(f"[Backend] Error en búsqueda: {str(e)}")
        import traceback
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"Error en búsqueda: {str(e)}")



@app.post("/descargar_por_id")
def descargar_por_id(request: dict):
    """
    Descarga canciones usando video_id de YouTube.
    
    Body: {"video_ids": ["dQw4w9WgXcQ", "..."]}
    """
    video_ids = request.get("video_ids", [])
    
    if not video_ids:
        raise HTTPException(status_code=400, detail="Lista video_ids vacía")
    
    import yt_dlp
    from app.utils import crear_directorio_si_no_existe
    
    crear_directorio_si_no_existe(DOWNLOAD_DIR)
    resultados = []
    
    for video_id in video_ids:
        try:
            url = f"https://youtube.com/watch?v={video_id}"
            
            opciones = {
                "format": "bestaudio/best",
                "outtmpl": os.path.join(DOWNLOAD_DIR, "%(title)s.%(ext)s"),
                "postprocessors": [
                    {
                        "key": "FFmpegExtractAudio",
                        "preferredcodec": "mp3",
                        "preferredquality": "192",
                    }
                ],
                "quiet": True,
                "noplaylist": True,
            }
            
            with yt_dlp.YoutubeDL(opciones) as ydl:
                info = ydl.extract_info(url, download=True)
                titulo = info.get('title', video_id)
                

                archivo_nombre = None
                for archivo in os.listdir(DOWNLOAD_DIR):
                    if archivo.endswith(".mp3") and titulo in archivo:
                        archivo_nombre = archivo
                        break
                
                resultados.append({
                    "nombre": video_id,
                    "url": url,
                    "archivo": archivo_nombre,
                    "estado": "descargado" if archivo_nombre else "error"
                })
        
        except Exception as e:
            resultados.append({
                "nombre": video_id,
                "url": "",
                "archivo": None,
                "estado": f"error: {str(e)}"
            })
    
    return {"resultados": resultados}



@app.get("/buscar_video")
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
        except Exception as e:  
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