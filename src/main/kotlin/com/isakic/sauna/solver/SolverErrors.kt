package com.isakic.sauna.solver

sealed class SolverError(msg: String) : Error(msg)

class MissingStartTile: SolverError("Map is missing the start tile")
class AmbiguousStartTile: SolverError("Map has more than one start tile")
