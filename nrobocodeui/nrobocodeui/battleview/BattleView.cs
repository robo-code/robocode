using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.Drawing.Drawing2D;
using System.Windows.Forms;
using java.util;
using nrobocodeui.manager;
using nrobocodeui.utils;
using robocode.battle;
using robocode.battlefield;
using robocode.manager;
using robocode.peer;
using robocode.ui;
using nrobocodeui.dialog;
using nrobocodeui.gfx;
using Random = System.Random;

namespace nrobocodeui.battleview
{
    public partial class BattleView : UserControl, IBattleView
    {
        #region Construction

        public BattleView()
        {
            this.battleField = new DefaultBattleField(800, 600);
            InitializeComponent();
        }

        public void InitBattleView(RobocodeManager manager, RobocodeFrame robocodeFrame)
        {
            this.manager = manager;
            this.robocodeFrame = robocodeFrame;
            battleViewProxy = new BattleViewProxy(this, robocodeFrame);
            imageManager = (ImageManager)manager.getImageManager();

            SetScale();
        }

        #endregion

        #region Private variables

        private RobocodeManager manager;
        private ImageManager imageManager;
        private RobocodeFrame robocodeFrame;
        private BattleViewProxy battleViewProxy;
        public BattleViewProxy BattleViewProxy
        {
            get
            {
                return battleViewProxy;
            }
        }

        private BattleField battleField;
        private Battle battle;

        #endregion

        #region Drawing variables

        private bool displayTPS;
        private bool displayFPS;
        private bool drawRobotName;
        private bool drawRobotEnergy;
        private bool drawScanArcs;
        private bool drawGround;
        private bool drawExplosions;
        private bool drawExplosionDebris;
        //TODO renderingHints = props.getRenderingHints();
        //TODO numBuffers = props.getOptionsRenderingNoBuffers();

        private double scale = 1.0;

        #endregion

        #region IBattleView Members

        public void setBattleField(BattleField bf)
        {
            battleField = bf;
        }

        public void setVisible(bool b)
        {
            //TODO
        }

        public void setInitialized(bool b)
        {
            //TODO
        }

        public void setBattle(Battle b)
        {
            battle = b;
        }

        public void repaint()
        {
            Invalidate();
        }

        public void setDisplayOptions()
        {
            RobocodeProperties props = manager.getProperties();

            displayTPS = props.getOptionsViewTPS();
            displayFPS = props.getOptionsViewFPS();
            drawRobotName = props.getOptionsViewRobotNames();
            drawRobotEnergy = props.getOptionsViewRobotEnergy();
            drawScanArcs = props.getOptionsViewScanArcs();
            drawGround = props.getOptionsViewGround();
            drawExplosions = props.getOptionsViewExplosions();
            drawExplosionDebris = props.getOptionsViewExplosionDebris();

            //TODO renderingHints = props.getRenderingHints();
            //TODO numBuffers = props.getOptionsRenderingNoBuffers();
        }

        public void update()
        {
            if (robocodeFrame.isIconified() || (getWidth() <= 0) || (getHeight() <= 0))
            {
                return;
            }
            Invalidate();
        }

        public bool isDisplayTPS()
        {
            return displayTPS;
        }

        public bool isDisplayFPS()
        {
            return displayFPS;
        }

        public int getWidth()
        {
            return Width;
        }

        public int getHeight()
        {
            return Height;
        }

        #endregion

        #region Helpers

        private void SetScale()
        {
            if ((getWidth() < battleField.getWidth() || getHeight() < battleField.getHeight()))
            {
                scale = Math.Min((double)getWidth() / battleField.getWidth(), (double)getHeight() / battleField.getHeight());
            }
            else
            {
                scale = 1;
            }
        }

        #endregion

        #region Events

        protected override void OnClientSizeChanged(EventArgs e)
        {
            base.OnClientSizeChanged(e);
            SetScale();
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            if (battle != null && battle.isRunning())
            {
                //TODO ? update();
                DrawBattle(e.Graphics);
            }
            else
            {
                paintRobocodeLogo(e.Graphics);
            }
        }

        #endregion

        #region Draw root

        private void DrawBattle(Graphics g)
        {
    		// Save the graphics state
	    	GraphicsState graphicsState = g.Save();

            // Reset transform
            g.Transform = new Matrix();

            // Reset clip
            g.Clip = new Region();

            // Clear canvas
            g.Clear(BackColor);

            // Calculate border space
            float dx = (float)(getWidth() - scale * battleField.getWidth()) / 2;
            float dy = (float)(getHeight() - scale * battleField.getHeight()) / 2;

            // Translate and scale the Graphics context
            Matrix at;
            at = new Matrix();
            at.Translate(dx, dy);
            at.Scale((float)scale, (float)scale);
            g.Transform = at;

            // Set the clip rectangle
            g.Clip = new Region(new Rectangle(0, 0, battleField.getWidth(), battleField.getHeight()));

            // Draw ground
            DrawGround(g);

            // Draw robots and debrises
            DrawRobots(g);

            // Draw the border of the battlefield
            DrawBorder(g);

            // Draw all bullets
            DrawBullets(g);

            // Draw all text
            DrawText(g);

            // Restore the graphics state
            g.Restore(graphicsState);
        }

        #endregion

        #region Robots

        public void DrawRobots(Graphics g)
        {
            List robots = battle.getRobots();
            object[] robotPeers = robots.toArray();

    		float x, y;
            Matrix at;
    		int battleFieldHeight = battle.getBattleField().getHeight();

            // Paint all explosion debrises for dead robots
	        if (drawGround && drawExplosionDebris && battle.isRobotsLoaded())
            {
                RenderImage explodeDebrise = new RenderImage(resources.images.ground.explode_debris);

                foreach (RobotPeer r in robotPeers)
                {
			        if (r.isDead())
                    {
				        x = (float)r.getX();
				        y = (float)(battleFieldHeight - r.getY());

                        at = new Matrix();
                        at.Translate(x, y);

				        explodeDebrise.Transform = at;
				        explodeDebrise.Paint(g);
			        }
		        }
	        }

            // Draw all robots
            foreach (RobotPeer r in robotPeers)
            {
                DrawRobot(r, g);
            }
        }

        private void DrawRobot(RobotPeer robot, Graphics g)
        {
            if (robot.isAlive())
            {
                float x = (float)robot.getX();
                float y = battleField.getHeight() - (float)robot.getY();
                bool droid = robot.isDroid();

                Matrix at;
                at = new Matrix();
                at.Translate(x, y);
                at.Rotate(Radians.ToDegrees(robot.getHeading()));

                RenderImage robotRenderImage = new RenderImage(imageManager.getColoredBodyRenderImage(robot.getBodyColor()));

                robotRenderImage.Transform = at;
                robotRenderImage.Paint(g);

                at = new Matrix();
                at.Translate(x, y);
                at.Rotate(Radians.ToDegrees(robot.getGunHeading()));

                RenderImage gunRenderImage = new RenderImage(imageManager.getColoredGunRenderImage(robot.getGunColor()));

                gunRenderImage.Transform = at;
                gunRenderImage.Paint(g);

                if (!droid)
                {
                    at = new Matrix();
                    at.Translate(x, y);
                    at.Rotate(Radians.ToDegrees(robot.getRadarHeading()));

                    RenderImage radarRenderImage = new RenderImage(imageManager.getColoredRadarRenderImage(robot.getRadarColor()));

                    radarRenderImage.Transform = at;
                    radarRenderImage.Paint(g);
                }
            }
        }

        #endregion

        #region Ground

        private void DrawGround(Graphics g)
        {
            if (!drawGround)
            {
                g.Clear(Color.Black);
            }
            else
            {
                // Create pre-rendered ground image if it is not available
                if (groundImage == null)
                {
                    CreateGroundImage();
                }

                // Draw the pre-rendered ground if it is available
                if (groundImage != null)
                {
                    int groundWidth = (int)(battleField.getWidth() * scale) + 1;
                    int groundHeight = (int)(battleField.getHeight() * scale) + 1;

                    int dx = (getWidth() - groundWidth) / 2;
                    int dy = (getHeight() - groundHeight) / 2;

                    Matrix savedTx = g.Transform.Clone();
                    g.Transform = new Matrix();

                    g.DrawImage(groundImage, dx, dy, groundWidth, groundHeight);

                    g.Transform = savedTx;
                }
            }
        }

        private void DrawBorder(Graphics g)
        {
            Region savedClip = g.Clip.Clone();
            g.Clip = new Region();

            g.DrawRectangle(new Pen(Color.Red), -1, -1, battleField.getWidth() + 2, battleField.getHeight() + 2);

            g.Clip = savedClip;
        }

        private Bitmap groundImage;
        private int[,] groundTiles;
        private int groundTileWidth = 64;
        private int groundTileHeight = 64;

        private void CreateGroundImage()
        {
            // Reinitialize ground tiles

            int NUM_HORZ_TILES = battleField.getWidth() / groundTileWidth + 1;
            int NUM_VERT_TILES = battleField.getHeight() / groundTileHeight + 1;

            if ((groundTiles == null) || (groundTiles.GetLength(0) != NUM_HORZ_TILES) || (groundTiles.GetLength(1) != NUM_VERT_TILES))
            {
                groundTiles = new int[NUM_HORZ_TILES, NUM_VERT_TILES];

                Random random = new Random();

                for (int y = NUM_VERT_TILES - 1; y >= 0; y--)
                {
                    for (int x = NUM_HORZ_TILES - 1; x >= 0; x--)
                    {
                        groundTiles[x, y] = random.Next(5);
                    }
                }
            }

            // Create new buffered image with the ground pre-rendered

            int groundWidth = (int)(battleField.getWidth() * scale);
            int groundHeight = (int)(battleField.getHeight() * scale);

            groundImage = new Bitmap(groundWidth, groundHeight, PixelFormat.Format24bppRgb);

            Graphics groundGfx = Graphics.FromImage(groundImage);

            // TODO: Scale the gfx on the bitmap

            for (int y = NUM_VERT_TILES - 1; y >= 0; y--)
            {
                for (int x = NUM_HORZ_TILES - 1; x >= 0; x--)
                {
                    Image img = resources.images.ground.blue_metal[groundTiles[x, y]];
                    if (img != null)
                    {
                        groundGfx.DrawImage(img, new Rectangle(x * groundTileWidth, y * groundTileHeight, img.Width, img.Height), 0, 0, img.Width, img.Height, GraphicsUnit.Pixel);
                    }
                }
            }
        }

        #endregion

        #region Bullets

	    private void DrawBullets(Graphics g) {
		    Region savedClip = g.Clip.Clone();

		    g.Clip = new Region();

		    float x, y;
            Matrix at;

            int battleFieldHeight = battle.getBattleField().getHeight();

            foreach (BulletPeer bullet in battle.getBullets().toArray())
            {
			    x = (float)bullet.getPaintX();
                y = (float)(battleFieldHeight - bullet.getPaintY());

                at = new Matrix();
                at.Translate(x, y);

			    if (bullet.getState() <= BulletPeer.STATE_MOVING)
                {
				    // radius = sqrt(x^2 / 0.1 * power), where x is the width of 1 pixel for a minimum 0.1 bullet
				    float scale = (float)Math.Max(2 * Math.Sqrt(2.5 * bullet.getPower()), 2 / this.scale);

				    at.Scale(scale, scale);

                    Region bulletArea = BULLET_AREA.Clone();
                    bulletArea.Transform(at);

                    java.awt.Color color = bullet.getColor();

                    Color bulletColor;
                    if (color == null)
                    {
                        bulletColor = Color.White;
				    }
                    else
                    {
                        bulletColor = Color.FromArgb(color.getRGB());
                    }


                    g.FillEllipse(new SolidBrush(bulletColor), bulletArea.GetBounds(g));

			    }
                else if (drawExplosions)
                {
				    if (!(bullet is ExplosionPeer))
                    {
					    float scale = (float)Math.Sqrt(1000 * bullet.getPower()) / 128;

					    at.Scale(scale, scale);
				    }

                    int index = bullet.getExplosionImageIndex();
                    int frame = bullet.getFrame();
                    Image img = resources.images.explosion.explosions[index,frame];
                    RenderImage explosionRenderImage = new RenderImage(img);

				    explosionRenderImage.Transform = at;
				    explosionRenderImage.Paint(g);
			    }
		    }

		    g.Clip = savedClip;
	    }

        private static readonly Region BULLET_AREA = new Region(new RectangleF(-0.5F, -0.5F, 1, 1));

        #endregion

        #region Text

	    private void DrawText(Graphics g)
        {
		    Region savedClip = g.Clip.Clone();

		    g.Clip = new Region();

            Font font = new Font(FontFamily.GenericSansSerif, (float)(10.0 / scale), FontStyle.Regular);
            Brush brush = new SolidBrush(Color.White);

            int battleFieldHeight = battle.getBattleField().getHeight();

            foreach (RobotPeer r in battle.getRobots().toArray())
            {
			    if (r.isDead()) {
				    continue;
			    }
			    float x = (float)r.getX();
                float y = (float)(battleFieldHeight - r.getY());

			    if (drawRobotEnergy && r.getRobot() != null)
                {
				    int ll = (int)r.getEnergy();
				    int rl = (int)((r.getEnergy() - ll + .001) * 10.0);

				    if (rl == 10)
                    {
					    rl = 9;
				    }

                    String energyString = ll + "." + rl;

				    if (r.getEnergy() == 0 && r.isAlive())
                    {
					    energyString = "Disabled";
				    }

                    CenterString(g, brush, energyString, x, y - ROBOT_TEXT_Y_OFFSET - font.Height / 2, font);
			    }
			    if (drawRobotName)
                {
                    CenterString(g, brush, r.getVeryShortName(), x, y + ROBOT_TEXT_Y_OFFSET + font.Height / 2, font);
			    }
			    if (r.isPaintEnabled() && r.getRobot() != null)
                {
// TODO:				    DrawRobotPaint(g, r);
			    }
		    }

            g.Clip = savedClip;
        }

        private const int ROBOT_TEXT_Y_OFFSET = 24;

        private void CenterString(Graphics g, Brush brush, String s, float x, float y, Font font)
        {
            SizeF measuredSize = g.MeasureString(s, font);

            float width = measuredSize.Width;
            float height = font.Height;

            float left = x - width / 2;
            float top = y - height / 2;

            float scaledViewWidth = (float)(Width / scale);
            float scaledViewHeight = (float)(Height / scale);

            float borderWidth = (scaledViewWidth - battleField.getWidth()) / 2;
            float borderHeight = (scaledViewHeight - battleField.getHeight()) / 2;

            if (left + width > scaledViewWidth)
            {
                left = scaledViewWidth - width;
            }
            if (top + height > scaledViewHeight)
            {
                top = scaledViewHeight - height;
            }
            if (left < -borderWidth)
            {
                left = -borderWidth;
            }
            if (top < -borderHeight)
            {
                top = -borderHeight;
            }

            g.DrawString(s, font, brush, left, top);
        }

        #endregion

        #region Logo

        public void paintRobocodeLogo(Graphics g)
        {
            Bitmap logo = resources.images.robocode_logo;
            g.DrawImage(logo, (Width - logo.Width) / 2, (Height - logo.Height) / 2);
        }

        #endregion
    }
}
