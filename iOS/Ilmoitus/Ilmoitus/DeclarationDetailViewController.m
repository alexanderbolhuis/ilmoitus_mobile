//
//  DeclarationDetailViewController.m
//  Ilmoitus
//
//  Created by Alexander Bolhuis on 22-04-14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import "DeclarationDetailViewController.h"
#import "DeclarationLine.h"

@interface DeclarationDetailViewController ()
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UILabel *leidinggevende;
@property (weak, nonatomic) IBOutlet UITextView *opmerking;

@property (strong, nonatomic) NSMutableArray *declarationLines;

@end

@implementation DeclarationDetailViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.declarationLines = [[NSMutableArray alloc]init];
    [self fill];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_declarationLines count];
}

/*
 #pragma mark - Navigation
 
 // In a storyboard-based application, you will often want to do a little preparation before navigation
 - (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
 {
 // Get the new view controller using [segue destinationViewController].
 // Pass the selected object to the new view controller.
 }
 */

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    DeclarationLine *line = (DeclarationLine *)[self.declarationLines objectAtIndex:indexPath];
    
    static NSString *simpleTableIdentifier = @"declarationLine";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
    
    UILabel *dateLabel = (UILabel *)[cell viewWithTag:1];
    UILabel *subTypeLabel = (UILabel *)[cell viewWithTag:2];
    UILabel *costLabel = (UILabel *)[cell viewWithTag:3];
    
    dateLabel.text = line.date;
    // TODO Get subtype
    subTypeLabel.text = @"Type";
    costLabel.text =[NSString stringWithFormat:@"€%.02f", line.cost];
    
    
    return cell;
}

-(void)fill
{
    DeclarationLine *line1 = [[DeclarationLine alloc]initWithDate:@"24-04-2014" SubType:@"Reiskosten" Cost:24.75];
    
    DeclarationLine *line2 = [[DeclarationLine alloc]initWithDate:@"20-04-2014" SubType:@"Studiekosten" Cost:314.15];
    
    DeclarationLine *line3 = [[DeclarationLine alloc]initWithDate:@"12-03-2014" SubType:@"Lunchkosten" Cost:3.14];
    
    DeclarationLine *line4 = [[DeclarationLine alloc]initWithDate:@"20-03-2014" SubType:@"Autokosten" Cost:358.93];
    
    [self.declarationLines addObjectsFromArray:@[line1, line2, line3, line4]];
}

@end