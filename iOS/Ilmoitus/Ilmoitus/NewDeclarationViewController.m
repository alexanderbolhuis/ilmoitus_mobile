//
//  SecondViewController.m
//  Ilmoitus
//
//  Created by Alexander Bolhuis on 22-04-14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import "NewDeclarationViewController.h"
#import "NewDeclarationLineViewController.h"
#import "Declaration.h"
#import "DeclarationLine.h"
#import "Supervisor.h"
#import "constants.h"
#import "Attachment.h"

@interface NewDeclarationViewController ()
@property (weak, nonatomic) IBOutlet UITextField *supervisor;
@property (nonatomic) NSMutableArray *supervisorList;
@property (weak, nonatomic) IBOutlet UITextView *comment;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (nonatomic) UIPickerView * pktStatePicker;
@property (nonatomic) UIToolbar *mypickerToolbar;
@property (weak, nonatomic) IBOutlet UILabel *totalPrice;

@end

@implementation NewDeclarationViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    self.supervisor.delegate = self;
    [self.comment setReturnKeyType: 	UIReturnKeyDone];
    self.comment.delegate = self;
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    
    [self getSupervisorList];
    
    if (self.declaration == nil) {
        self.declaration = [[Declaration alloc] init];
    }
    [self decalrationLinesChanged];
    self.comment.text = self.declaration.comment;
    
    // Textfield delegates
    self.supervisor.delegate = self;
    [self.comment setReturnKeyType: UIReturnKeyDone];
    self.comment.delegate = self;
    
    // TextView styling
    [self.comment.layer setBorderColor:[[[UIColor grayColor] colorWithAlphaComponent:0.5] CGColor]];
    [self.comment.layer setBorderWidth:0.5];
    self.comment.layer.cornerRadius = 5;
    self.comment.clipsToBounds = YES;
    
    [self.supervisor addTarget:self
                    action:@selector(textFieldDidChange)
          forControlEvents:UIControlEventEditingChanged];
    
    // Create SupervisorPicker
    self.supervisorList = [[NSMutableArray alloc] init];
    
    self.pktStatePicker = [[UIPickerView alloc] initWithFrame:CGRectMake(0, 43, 320, 480)];
    
    self.pktStatePicker.delegate = self;
    
    self.pktStatePicker.dataSource = self;
    
    [self.pktStatePicker  setShowsSelectionIndicator:YES];
    
    self.supervisor.inputView =  self.pktStatePicker  ;
    
    // Create done button in UIPickerView
    self.mypickerToolbar = [[UIToolbar alloc] initWithFrame:CGRectMake(0, 0, 320, 56)];
    
    self.mypickerToolbar.tintColor = [UIColor whiteColor];
    self.mypickerToolbar.barTintColor = [UIColor colorWithRed:(189/255.0) green:(26/255.0) blue:(47/255.0) alpha:1.0];
    
    [self.mypickerToolbar sizeToFit];
    
    
    NSMutableArray *barItems = [[NSMutableArray alloc] init];
    
    
    UIBarButtonItem *flexSpace = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:self action:nil];
    
    [barItems addObject:flexSpace];
    
    
    UIBarButtonItem *doneBtn = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(pickerDoneClicked)];
    
    [barItems addObject:doneBtn];
    
    
    [self.mypickerToolbar setItems:barItems animated:YES];
    
    
    self.supervisor.inputAccessoryView = self.mypickerToolbar;
}

- (IBAction)postDeclaration:(id)sender {
    self.declaration.className = @"open_declaration";
    self.declaration.status = @"Open";
        // TODO get current date in right format
    self.declaration.createdAt = @"2014-05-15 07:27:33.448849";
    self.declaration.createdBy = [[[NSUserDefaults standardUserDefaults] stringForKey:@"person_id"] longLongValue];
    
    // TODO Get supervisor from dropdown
    NSMutableArray *at = [[NSMutableArray alloc]init];
    [at addObject:[NSNumber numberWithLongLong:[[[NSUserDefaults standardUserDefaults] stringForKey:@"supervisor"] longLongValue]]];
    self.declaration.assignedTo = at;
    self.declaration.comment = self.comment.text;
    // TODO Calc price and items
    self.declaration.itemsTotalPrice = 30.00;
    self.declaration.itemsCount = 2;
    [self saveDeclaration];
}

-(void)viewDidAppear:(BOOL)animated
{}

-(IBAction)unwindToNewDeclaration:(UIStoryboardSegue *)segue
{
    NewDeclarationLineViewController * source = [segue sourceViewController];
    if(source.declarationLine != nil)
    {
        [self.declaration.lines addObject:source.declarationLine];
        [self decalrationLinesChanged];
    }
    if(source.attachment != nil)
    {
        [self.declaration.attachments addObject:source.attachment];
    }
}

-(void)textFieldDidChange
{
    NSLog( @"Supervisor TextField changed: %@", self.supervisor.text);
    
    for (int i = 0; i < [self.supervisorList count]; i++){
        Supervisor *supervisor = [self.supervisorList objectAtIndex:i];
        if ([NSString stringWithFormat:@"%@ %@", supervisor.first_name, supervisor.last_name] == self.supervisor.text) {
            [self.declaration.assignedTo addObject:[NSNumber numberWithLongLong:supervisor.ident]];
            [self.pktStatePicker selectRow:i inComponent:0 animated:YES];
        }
    }
}

-(void)pickerDoneClicked

{
  	NSLog(@"Supervisor Picker Done Clicked");
    
    [self.supervisor resignFirstResponder];
}

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView

{
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component

{
    return [self.supervisorList count];
}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    Supervisor *sup = [self.supervisorList objectAtIndex:row];
    return [NSString stringWithFormat:@"%@ %@ (%d)", sup.first_name, sup.last_name, sup.employee_number];
}

- (void) pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    Supervisor *sup = [self.supervisorList objectAtIndex:row];
    self.supervisor.text = @"";
    [self.supervisor insertText:[NSString stringWithFormat:@"%@ %@ (%d)", sup.first_name, sup.last_name, sup.employee_number]];
}

-(BOOL)textViewShouldEndEditing:(UITextView *)textView {
    self.declaration.comment = self.comment.text;
    return YES;
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    if([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
    }
    
    return YES;
}

- (IBAction)cancelDeclaration:(id)sender {
    self.declaration = nil;
    [self viewDidLoad];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)saveDeclaration
{
    Declaration *decl = self.declaration;
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] stringForKey:@"token"] forHTTPHeaderField:@"Authorization"];
    
    // Lines
    NSMutableArray *declarationlines = [[NSMutableArray alloc] init];
    for (DeclarationLine *line in decl.lines)
    {
        
        //todo remove{
        line.subtype.ident = 4656664208736256;
        //}
        NSDictionary *currentline = @{@"receipt_date": line.date, @"cost":[NSNumber numberWithFloat:line.cost], @"declaration_sub_type":[NSNumber numberWithLongLong:line.subtype.ident]};
        [declarationlines addObject:currentline];
    }
    
    // Attachments
    NSMutableArray *attachments = [[NSMutableArray alloc] init];
    for (Attachment *attachment in self.declaration.attachments)
    {
        NSDictionary *currentAttachment = @{@"name":attachment.name, @"file":attachment.data};
        [attachments addObject:currentAttachment];
    }
    
    // Declaration
    NSDictionary *declaration = @{@"state":decl.status, @"created_at":decl.createdAt, @"created_by":[NSNumber numberWithLongLong:decl.createdBy], @"supervisor":[decl.assignedTo firstObject], @"comment":decl.comment, @"items_total_price":[NSNumber numberWithFloat:decl.itemsTotalPrice], @"items_count":[NSNumber numberWithInt:decl.itemsCount], @"lines":declarationlines, @"attachments":attachments};
    
    
    // Total dict
    NSDictionary *params = @{@"declaration":declaration};
    
    NSLog(@"JSON data that is going to be saved/sent: %@",params);
    
    NSString *url = [NSString stringWithFormat:@"%@/declaration", baseURL];
    AFHTTPRequestOperation *apiRequest = [manager POST:url parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSError* error;
        NSDictionary* json = [NSJSONSerialization
                              JSONObjectWithData:responseObject
                              
                              options:kNilOptions
                              error:&error];
        
        NSLog(@"JSON response data for saving declaration: %@",json);
        // Handle success
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"Error while saving declaration: %@", error);
        // Handle error
        
    }];
    
    [apiRequest start];
}

-(void)getSupervisorList
{
    // Do Request
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFHTTPRequestSerializer serializer];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] stringForKey:@"token"] forHTTPHeaderField:@"Authorization"];
    NSString *url = [NSString stringWithFormat:@"%@/current_user/supervisors", baseURL];
    [manager GET:url parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSError* error;
        NSDictionary* json = [NSJSONSerialization
                              JSONObjectWithData:responseObject
                              
                              options:kNilOptions
                              error:&error];
        
        NSLog(@"JSON response: %@", json);
        
        NSMutableArray *supervisorsFound = [[NSMutableArray alloc] init];
        for (NSDictionary *supervisor in json) {
            Supervisor *sup = [[Supervisor alloc] init];
            sup.ident = [supervisor[@"id"]longLongValue];
            sup.class_name = supervisor[@"class_name"];
            sup.first_name = supervisor[@"first_name"];
            sup.last_name = supervisor[@"last_name"];
            sup.email = supervisor[@"email"];
            sup.employee_number = [supervisor[@"employee_number"] integerValue];
            sup.department = [supervisor[@"department"] longLongValue];
            sup.supervisor = [supervisor[@"supervisor"] longLongValue];
            sup.max_declaration_price = [supervisor[@"max_declaration_price"] floatValue];
            
            [supervisorsFound addObject:sup];
            
            // Set default supervisor
            if ((sup.ident == [[[NSUserDefaults standardUserDefaults] stringForKey:@"supervisor"] longLongValue]) && ([self.declaration.assignedTo firstObject] == nil)) {
                self.supervisor.text = @"";
                NSString *spv = [NSString stringWithFormat:@"%@ %@", sup.first_name, sup.last_name];
                [self.supervisor insertText:spv];
            }
        }
        self.supervisorList = supervisorsFound;
        
        [self.pktStatePicker reloadAllComponents];
        
        // TODO create dropdown to select supervisor
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"Error while getting supervisor list: %@", error);
    }];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.declaration.lines count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"DeclarationLineCell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:CellIdentifier];
    }
    
    DeclarationLine *line = [self.declaration.lines objectAtIndex:indexPath.row];
    
    UILabel *label = (UILabel *)[cell viewWithTag:1];
    label.text = [NSString stringWithFormat:@"%@ - %@ - €%.2f", line.date, @"Decalration(Sub)Type", line.cost];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        [self.declaration.lines removeObjectAtIndex:indexPath.row];
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
        [self setTotalPrice];
    }
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}

- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return NO;
}

-(void)decalrationLinesChanged
{
    [self.tableView reloadData];
    [self setTotalPrice];
}

-(void)setTotalPrice
{
    NSString* formattedAmount = [NSString stringWithFormat:@"%.2f", self.declaration.calculateTotalPrice];
    self.totalPrice.text = [NSString stringWithFormat:@"Totaal bedrag: €%@", formattedAmount];

}
@end
