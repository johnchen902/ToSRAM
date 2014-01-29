package tosram.view;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.List;
import tosram.Direction;
import tosram.Path;
import tosram.RoundPath;

class PathPanelCalculator {
	static class Result {
		private Path2D path;
		private List<Shape> segements;

		Result(Path2D path, List<Shape> segements) {
			this.path = path;
			this.segements = segements;
		}

		Path2D getPath() {
			return path;
		}

		void setPath(Path2D path) {
			this.path = path;
		}

		List<Shape> getSegements() {
			return segements;
		}

		void setSegements(List<Shape> segements) {
			this.segements = segements;
		}
	}

	static Result calculatePath(Path path, double cellWidth, double cellHeight) {
		RoundPath rpath = new RoundPath();
		int x = path.getBeginPoint().x, y = path.getBeginPoint().y;
		rpath.moveTo((x + .5) * cellWidth, (y + .5) * cellHeight);
		for (Direction dir : path.getDirections()) {
			switch (dir) {
			case WEST:
			case WEST_NORTH:
			case WEST_SOUTH:
				x--;
				break;
			case EAST:
			case EAST_NORTH:
			case EAST_SOUTH:
				x++;
				break;
			case NORTH:
			case SOUTH:
				break;
			}
			switch (dir) {
			case SOUTH:
			case WEST_SOUTH:
			case EAST_SOUTH:
				y++;
				break;
			case NORTH:
			case WEST_NORTH:
			case EAST_NORTH:
				y--;
				break;
			case WEST:
			case EAST:
				break;
			}
			rpath.roundTo((x + .5) * cellWidth, (y + .5) * cellHeight);
		}
		return new Result(rpath.getPath(), rpath.getSegements());
	}
}
