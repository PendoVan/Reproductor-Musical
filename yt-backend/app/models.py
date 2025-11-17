from pydantic import BaseModel
from typing import List

class CancionesRequest(BaseModel):
    """Petición para descargar una o varias canciones por nombre."""
    canciones: List[str]
