package tosram;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>MovingPathGenerator</code> that produce the simplest path.
 * 
 * @author johnchen902
 */
public class BasicMovingPathGenerator implements MovingPathGenerator {

	private Point toScreen(Rectangle r, Dimension d, Point p) {
		return new Point(r.x + r.width * (2 * p.x + 1) / 2 / d.width, r.y
				+ r.height * (2 * p.y + 1) / 2 / d.height);
	}

	@Override
	public List<Move> getMovePath(Path path, Rectangle screen,
			Dimension mapSize, int time, boolean startImmediately) {
		List<Move> moves = new ArrayList<Move>();
		Point p = new Point(path.getBeginPoint());
		int timePerStep = time / path.getDirections().size();
		if (timePerStep > 100)
			timePerStep = 100;
		moves.add(new Move(toScreen(screen, mapSize, p),
				startImmediately ? timePerStep : 3000));
		for (Direction dir : path.getDirections()) {
			p.x += dir.getX();
			p.y += dir.getY();
			moves.add(new Move(toScreen(screen, mapSize, p), timePerStep));
		}
		return moves;
	}
}
